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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiBridgeClient aiBridgeClient;
    private final EncryptionService encryptionService;
    private final AiMapper aiMapper;
    private final TaskRepository taskRepository;
    private final EdgeRepository edgeRepository;
    private final ProjectRepository projectRepository;
    private final AuditEventPublisher auditEventPublisher;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    @Transactional
    public void generateSkeletonForProject(Project project, AiRequestConfig requestConfig) {
        ProviderConfig providerConfig = resolveProviderConfig(requestConfig, project.getOwner());
        GenerateSkeletonRequest request = aiMapper.toGenerateSkeletonRequest(project, providerConfig);

        try {
            GenerateSkeletonResponse response = aiBridgeClient.generateSkeleton(internalSecret, request);
            processSkeletonResponse(project, response);
            auditEventPublisher.publishAiSkeletonGeneratedEvent(this, project, response);
        } catch (AiBridgeErrorDecoder.AiProviderException | AiBridgeErrorDecoder.AiValidationException e) {
            log.error("Ошибка от AiBridge при генерации скелета для проекта {}: {}", project.getId(), e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации скелета для проекта {}", project.getId(), e);
            throw new BadRequestException("Внутренняя ошибка сервера при обращении к AI-сервису.");
        }
    }

    private void processSkeletonResponse(Project project, GenerateSkeletonResponse response) {
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
                    if (source == null || target == null) {
                        return null;
                    }
                    return Edge.builder()
                            .project(project)
                            .sourceTask(source)
                            .targetTask(target)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        edgeRepository.saveAll(edges);

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);
        log.info("Скелет для проекта {} успешно сгенерирован и сохранен.", project.getId());
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