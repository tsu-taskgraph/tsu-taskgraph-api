package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.client.AiBridgeClient;
import ru.tsu_taskgraph.core_api.client.AiBridgeErrorDecoder;
import ru.tsu_taskgraph.core_api.dto.ai.ProviderCheckRequest;
import ru.tsu_taskgraph.core_api.dto.ai.ProviderConfig;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai/providers")
@RequiredArgsConstructor
@Tag(name = "AI", description = "Эндпоинты, вызывающие AI Service Bridge")
public class AiProviderController {

    private final AiBridgeClient aiBridgeClient;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    @GetMapping
    @Operation(summary = "Получить список доступных AI-провайдеров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список провайдеров получен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при обращении к AI-сервису (например, сервис недоступен).")
    })
    public List<String> getProviders() {
        try {
            return aiBridgeClient.getProviders(internalSecret);
        } catch (Exception e) {
            log.error("Ошибка при получении списка AI-провайдеров от ai-bridge", e);
            throw new BadRequestException("Не удалось получить список AI-провайдеров: " + e.getMessage());
        }
    }

    @PostMapping("/models")
    @Operation(summary = "Получить список моделей для конкретного провайдера и ключа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список моделей получен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при обращении к AI-сервису. Возможные причины:\n" +
                    "* 400: Неверные входные данные (например, не указан провайдер).\n" +
                    "* 401: Невалидный API-ключ для выбранного провайдера.\n" +
                    "* 429: Превышен лимит запросов (Rate limit) у провайдера.\n" +
                    "* 502: Сбой на стороне AI-провайдера.\n" +
                    "* 504: Превышено время ожидания ответа от AI-провайдера.")
    })
    public List<String> getModels(@RequestBody ProviderConfig providerConfig) {
        ProviderCheckRequest request = ProviderCheckRequest.builder()
                .providerConfig(providerConfig)
                .build();
        try {
            return aiBridgeClient.getModels(internalSecret, request);
        } catch (AiBridgeErrorDecoder.AiProviderException | AiBridgeErrorDecoder.AiValidationException e) {
            log.error("Ошибка от AiBridge при получении моделей: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при получении моделей от ai-bridge", e);
            throw new BadRequestException("Внутренняя ошибка сервера при обращении к AI-сервису.");
        }
    }
}