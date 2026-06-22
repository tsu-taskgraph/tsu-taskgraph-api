package ru.tsu_taskgraph.core_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.AiProvider;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedAiSettings {
    private AiProvider provider;
    private String model;
    private String apiKeyMasked;
    private boolean hasApiKey;
    private String ollamaBaseUrl;
}