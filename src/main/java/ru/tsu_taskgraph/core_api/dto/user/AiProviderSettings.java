package ru.tsu_taskgraph.core_api.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProviderSettings {
    @Min(0)
    @Max(2)
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
