package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.tsu_taskgraph.core_api.client.AiBridgeClient;
import ru.tsu_taskgraph.core_api.client.AiBridgeErrorDecoder;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.ai.*;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.mapper.AiMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.ProjectRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.util.CycleDetector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private static final int MAX_RECOVERY_RETRIES = 3;

    private final AiBridgeClient aiBridgeClient;
    private final EncryptionService encryptionService;
    private final AiMapper aiMapper;
    private final TaskRepository taskRepository;
    private final EdgeRepository edgeRepository;
    private final ProjectRepository projectRepository;
    private final AuditEventPublisher auditEventPublisher;
    private final CycleDetector cycleDetector;
    private final SmartRecoveryService smartRecoveryService;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    @Transactional
    public void generateSkeletonForProject(Project project, AiRequestConfig requestConfig) {
        ProviderConfig providerConfig = resolveProviderConfig(requestConfig, project.getOwner());
        GenerateSkeletonRequest request = aiMapper.toGenerateSkeletonRequest(project, providerConfig);

        try {
            GenerateSkeletonResponse response = aiBridgeClient.generateSkeleton(internalSecret, request);
            processSkeletonResponse(project, response, providerConfig);
            auditEventPublisher.publishAiSkeletonGeneratedEvent(this, project, response);
        } catch (AiBridgeErrorDecoder.AiProviderException | AiBridgeErrorDecoder.AiValidationException e) {
            log.error("Ошибка от AiBridge при генерации скелета для проекта {}: {}", project.getId(), e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации скелета для проекта {}", project.getId(), e);
            throw new BadRequestException("Внутренняя ошибка сервера при обращении к AI-сервису.");
        }
    }

    private void processSkeletonResponse(Project project, GenerateSkeletonResponse response, ProviderConfig providerConfig) {
        for (int i = 0; i < MAX_RECOVERY_RETRIES; i++) {
            Map<String, Task> tempIdToTaskMap = new HashMap<>();
            List<Task> tasks = buildTasksFromNodes(project, response.getNodes(), tempIdToTaskMap);
            List<Edge> edges = buildEdgesFromDto(project, response.getEdges(), tempIdToTaskMap);

            List<UUID> cycle = cycleDetector.findCycle(edges);
            if (cycle.isEmpty()) {
                // Цикла нет, сохраняем и выходим
                taskRepository.saveAll(tasks);
                edgeRepository.saveAll(edges);
                project.setStatus(ProjectStatus.ACTIVE);
                projectRepository.save(project);
                log.info("Скелет для проекта {} успешно сгенерирован и сохранен.", project.getId());
                return;
            }

            // Цикл найден, пытаемся исправить
            log.warn("Обнаружен цикл в сгенерированном графе для проекта {}. Попытка #{}", project.getId(), i + 1);
            SmartRecoveryRequest recoveryRequest = createRecoveryRequest(project, response, cycle, providerConfig);
            SmartRecoveryResponse recoveryResponse = smartRecoveryService.recover(recoveryRequest);
            
            // Обновляем граф для следующей итерации
            response.setNodes(recoveryResponse.getFixedPatch().getNewNodes());
            response.setEdges(recoveryResponse.getFixedPatch().getNewEdges());
        }

        // Если после всех попыток цикл не устранен
        throw new BadRequestException("ИИ не смог сгенерировать корректный граф задач после " + MAX_RECOVERY_RETRIES + " попыток.");
    }

    private List<Task> buildTasksFromNodes(Project project, List<SkeletonNode> nodes, Map<String, Task> tempIdToTaskMap) {
        return nodes.stream()
                .map(node -> {
                    Task task = Task.builder()
                            .project(project)
                            .title(node.getTitle())
                            .description(node.getDescription())
                            .category(node.getCategory())
                            .estimatedHours(node.getEstimatedHours())
                            .status(TaskStatus.LOCKED)
                            .build();
                    tempIdToTaskMap.put(node.getTempId(), task);
                    return task;
                })
                .collect(Collectors.toList());
    }

    private List<Edge> buildEdgesFromDto(Project project, List<SkeletonEdge> edgeDtos, Map<String, Task> tempIdToTaskMap) {
        return edgeDtos.stream()
                .map(edgeDto -> {
                    Task source = tempIdToTaskMap.get(edgeDto.getSourceTempId());
                    Task target = tempIdToTaskMap.get(edgeDto.getTargetTempId());
                    if (source == null || target == null) return null;
                    return Edge.builder()
                            .project(project)
                            .sourceTask(source)
                            .targetTask(target)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SmartRecoveryRequest createRecoveryRequest(Project project, GenerateSkeletonResponse response, List<UUID> cycle, ProviderConfig providerConfig) {
        MutationPatch failedMutation = MutationPatch.builder()
                .newNodes(response.getNodes())
                .newEdges(response.getEdges())
                .build();

        return SmartRecoveryRequest.builder()
                .currentGraph(null) // Граф еще не существует
                .failedMutation(failedMutation)
                .cycleNodes(cycle)
                .projectName(project.getName())
                .techStack(project.getTechStack())
                .aiEstimate(project.getAiEstimate())
                .providerConfig(providerConfig)
                .build();
    }

    private ProviderConfig resolveProviderConfig(AiRequestConfig requestConfig, User user) {
        // Способ 1: Из заголовков
        if (StringUtils.hasText(requestConfig.getProvider())) {
            return ProviderConfig.builder()
                    .provider(requestConfig.getProvider())
                    .apiKey(requestConfig.getApiKey())
                    .model(requestConfig.getModel())
                    .ollamaBaseUrl(requestConfig.getOllamaBaseUrl())
                    .settings(ProviderSettings.builder().build())
                    .build();
        }

        // Способ 2: Из профиля пользователя
        AiSettings userSettings = user.getAiSettings();
        if (userSettings != null && userSettings.getProvider() != null) {
            String decryptedApiKey = null;
            if (StringUtils.hasText(userSettings.getEncryptedApiKey())) {
                decryptedApiKey = encryptionService.decrypt(userSettings.getEncryptedApiKey());
            }
            return ProviderConfig.builder()
                    .provider(userSettings.getProvider().name())
                    .apiKey(decryptedApiKey)
                    .model(userSettings.getModel())
                    .ollamaBaseUrl(userSettings.getOllamaBaseUrl())
                    .settings(aiMapper.toDto(userSettings.getAiProviderSettings()))
                    .build();
        }

        throw new BadRequestException("AI-провайдер не сконфигурирован. Передайте настройки в X-AI-* заголовках или сохраните их в профиле.");
    }
}