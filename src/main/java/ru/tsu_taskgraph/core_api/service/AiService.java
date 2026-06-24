package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
    private final TaskStatusService taskStatusService;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Transactional
    public void generateSkeletonForProject(Project project, AiRequestConfig requestConfig) {
        ProviderConfig providerConfig = resolveProviderConfig(requestConfig, project.getOwner());
        GenerateSkeletonRequest request = aiMapper.toGenerateSkeletonRequest(project, providerConfig);

        try {
            GenerateSkeletonResponse response = aiBridgeClient.generateSkeleton(internalSecret, request);
            processSkeletonResponse(project, response, providerConfig);
            auditEventPublisher.publishAiSkeletonGeneratedEvent(this, project, response);
            triggerTasksEnrichment(project, providerConfig);
        } catch (AiBridgeErrorDecoder.AiProviderException | AiBridgeErrorDecoder.AiValidationException e) {
            log.error("Ошибка от AiBridge при генерации скелета для проекта {}: {}", project.getId(), e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации скелета для проекта {}", project.getId(), e);
            throw new BadRequestException("Внутренняя ошибка сервера при обращении к AI-сервису.");
        }
    }

    public MutationPatch getMutationPatch(Project project, String prompt, AiRequestConfig aiConfig) {
        ProviderConfig providerConfig = resolveProviderConfig(aiConfig, project.getOwner());
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        List<Edge> edges = edgeRepository.findByProjectId(project.getId());
        GraphSnapshot snapshot = aiMapper.createGraphSnapshot(tasks, edges);

        AiMutateGraphRequest mutateRequest = AiMutateGraphRequest.builder()
                .currentGraph(snapshot)
                .prompt(prompt)
                .projectName(project.getName())
                .techStack(project.getTechStack())
                .aiEstimate(project.getAiEstimate())
                .providerConfig(providerConfig)
                .build();

        AiMutateGraphResponse mutateResponse = aiBridgeClient.mutateGraph(internalSecret, mutateRequest);
        return mutateResponse.getPatch();
    }

    @Async
    public void triggerTasksEnrichment(Project project, ProviderConfig providerConfig) {
        log.info("Запуск обогащения задач для проекта {}", project.getId());
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        tasks.forEach(task -> {
            try {
                EnrichTaskRequest request = buildEnrichRequest(project, task, providerConfig);
                aiBridgeClient.enrichTask(internalSecret, request);
            } catch (Exception e) {
                log.error("Не удалось запустить обогащение для задачи {}", task.getId(), e);
            }
        });
    }

    private EnrichTaskRequest buildEnrichRequest(Project project, Task task, ProviderConfig providerConfig) {
        List<String> predecessorTitles = edgeRepository.findByTargetTask(task).stream()
                .map(Edge::getSourceTask)
                .map(Task::getTitle)
                .collect(Collectors.toList());

        List<String> successorTitles = edgeRepository.findBySourceTask(task).stream()
                .map(Edge::getTargetTask)
                .map(Task::getTitle)
                .collect(Collectors.toList());

        TaskContext taskContext = TaskContext.builder()
                .taskId(task.getId())
                .taskTitle(task.getTitle())
                .taskDescription(task.getDescription())
                .category(task.getCategory())
                .predecessorTitles(predecessorTitles)
                .successorTitles(successorTitles)
                .estimatedHours(task.getEstimatedHours())
                .build();

        String callbackUrl = appBaseUrl + "/api/v1/internal/enrichment-callback";

        return EnrichTaskRequest.builder()
                .projectName(project.getName())
                .techStack(project.getTechStack())
                .task(taskContext)
                .callbackUrl(callbackUrl)
                .generateWikiDraft(true)
                .providerConfig(providerConfig)
                .build();
    }

    private void processSkeletonResponse(Project project, GenerateSkeletonResponse response, ProviderConfig providerConfig) {
        for (int i = 0; i < MAX_RECOVERY_RETRIES; i++) {
            List<String> cycle = cycleDetector.findCycleInSkeleton(response.getNodes(), response.getEdges());

            if (cycle.isEmpty()) {
                // Цикла нет, сохраняем и выходим
                saveSkeleton(project, response);
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

    private void saveSkeleton(Project project, GenerateSkeletonResponse response) {
        Map<String, Task> tempIdToTaskMap = new HashMap<>();
        List<Task> tasks = response.getNodes().stream()
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
        taskRepository.saveAll(tasks);

        List<Edge> edges = response.getEdges().stream()
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
        edgeRepository.saveAll(edges);

        log.info("Attempting to unlock initial tasks for project {}", project.getId());
        tasks.forEach(taskStatusService::tryToUnlockTask);
        taskRepository.saveAll(tasks);

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);
        log.info("Скелет для проекта {} успешно сгенерирован и сохранен.", project.getId());
    }

    private SmartRecoveryRequest createRecoveryRequest(Project project, GenerateSkeletonResponse response, List<String> cycle, ProviderConfig providerConfig) {
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

    public ProviderConfig resolveProviderConfig(AiRequestConfig requestConfig, User user) {
        // Способ 1: Из заголовков
        if (StringUtils.hasText(requestConfig.getProvider())) {
            return ProviderConfig.builder()
                    .provider(requestConfig.getProvider())
                    .apiKey(requestConfig.getApiKey())
                    .model(requestConfig.getModel())
                    .customBaseUrl(requestConfig.getCustomBaseUrl())
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
                    .customBaseUrl(userSettings.getCustomBaseUrl())
                    .build();
        }

        throw new BadRequestException("AI-провайдер не сконфигурирован. Передайте настройки в X-AI-* заголовках или сохраните их в профиле.");
    }
}
