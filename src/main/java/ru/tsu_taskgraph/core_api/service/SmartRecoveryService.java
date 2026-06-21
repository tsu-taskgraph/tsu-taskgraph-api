package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tsu_taskgraph.core_api.client.AiBridgeClient;
import ru.tsu_taskgraph.core_api.client.AiBridgeErrorDecoder;
import ru.tsu_taskgraph.core_api.dto.ai.SmartRecoveryRequest;
import ru.tsu_taskgraph.core_api.dto.ai.SmartRecoveryResponse;
import ru.tsu_taskgraph.core_api.exception.AiCycleException;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartRecoveryService {

    private final AiBridgeClient aiBridgeClient;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    public SmartRecoveryResponse recover(SmartRecoveryRequest request) {
        log.info("Запрос Smart Recovery для проекта {}", request.getProjectName());
        try {
            return aiBridgeClient.smartRecovery(internalSecret, request);
        } catch (AiBridgeErrorDecoder.AiValidationException e) {
            log.warn("Smart Recovery не удался (ошибка 422 от AiBridge): {}", e.getMessage());
            throw new AiCycleException("ИИ не смог автоматически устранить циклическую зависимость.", request.getCycleNodes(), false);
        } catch (AiBridgeErrorDecoder.AiProviderException e) {
            log.error("Ошибка от AiBridge при попытке Smart Recovery: {}", e.getMessage());
            throw new BadRequestException("AI-сервис не смог исправить цикл: " + e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при вызове Smart Recovery", e);
            throw new BadRequestException("Внутренняя ошибка сервера при обращении к AI-сервису.");
        }
    }
}