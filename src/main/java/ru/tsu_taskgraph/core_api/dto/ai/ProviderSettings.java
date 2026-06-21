package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderSettings {
    private Double temperature;
    private Integer maxTokens;
    private Integer thinkingBudget;
    private Boolean enableWebSearch;
    private Boolean extendedThinking;
    private Integer thinkingTokenBudget;
    private String reasoningEffort;
    private String groqReasoningFormat;
    private Integer ollamaNumCtx;
    private Integer ollamaNumGpu;
}