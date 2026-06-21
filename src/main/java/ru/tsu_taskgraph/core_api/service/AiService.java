package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.tsu_taskgraph.core_api.client.AiBridgeClient;
import ru.tsu_taskgraph.core_api.dto.ai.AiRequestConfig;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonRequest;
import ru.tsu_taskgraph.core_api.dto.ai.ProviderConfig;
import ru.tsu_taskgraph.core_api.dto.ai.ProviderSettings;
import ru.tsu_taskgraph.core_api.entity.AiSettings;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.mapper.AiMapper;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiBridgeClient aiBridgeClient;
    private final EncryptionService encryptionService;
    private final AiMapper aiMapper;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    public void triggerSkeletonGeneration(Project project, AiRequestConfig requestConfig) {
        ProviderConfig providerConfig = resolveProviderConfig(requestConfig, project.getOwner());
        generateSkeletonAsync(project, providerConfig);
    }

    @Async
    public void generateSkeletonAsync(Project project, ProviderConfig providerConfig) {
        GenerateSkeletonRequest request = aiMapper.toGenerateSkeletonRequest(project, providerConfig);
        aiBridgeClient.generateSkeleton(internalSecret, request);
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