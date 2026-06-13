package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tsu_taskgraph.core_api.dto.user.AiProviderDto;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai-providers")
@Tag(name = "Users", description = "Профиль и AI-настройки текущего пользователя")
public class AiProviderController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Справочник провайдеров (модели, возможности, настройки)")
    public Map<String, AiProviderDto> listAiProviders() {
        return Map.of();
    }
}
