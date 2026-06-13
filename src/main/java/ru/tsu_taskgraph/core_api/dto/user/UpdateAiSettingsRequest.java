package ru.tsu_taskgraph.core_api.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.AiProvider;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAiSettingsRequest {
    @NotNull
    private AiProvider provider;
    private String model;
    private String apiKey;
    private String ollamaBaseUrl;
    private AiProviderSettings providerSettings;
}
