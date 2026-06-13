package ru.tsu_taskgraph.core_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProviderDto {
    private String defaultModel;
    private List<String> supportedModels;
    private Boolean supportsWebSearch;
    private Boolean supportsExtendedThinking;
    private Boolean supportsReasoningEffort;
    private Map<String, Object> settingsSchema;
}
