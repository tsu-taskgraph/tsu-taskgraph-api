package ru.tsu_taskgraph.core_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ai_provider_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiProviderSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Общие
    private Double temperature;
    private Integer maxTokens;

    // Gemini
    private Integer thinkingBudget;
    private Boolean enableWebSearch;

    // Anthropic
    private Boolean extendedThinking;
    private Integer thinkingTokenBudget;

    // OpenAI
    private String reasoningEffort; // low, medium, high

    // Groq
    private String groqReasoningFormat; // parsed, raw, hidden

    // Ollama
    private Integer ollamaNumCtx;
    private Integer ollamaNumGpu;

    @OneToOne(mappedBy = "aiProviderSettings")
    private AiSettings aiSettings;
}