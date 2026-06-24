package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestConfig {
    private String provider;
    private String apiKey;
    private String model;
    private String customBaseUrl;
}