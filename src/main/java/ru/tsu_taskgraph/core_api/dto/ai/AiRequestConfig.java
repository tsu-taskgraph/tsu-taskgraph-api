package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AiRequestConfig {
    private String provider;
    private String apiKey;
    private String model;
    private String ollamaBaseUrl;
}
