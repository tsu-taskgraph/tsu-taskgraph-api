package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderConfig {
    private String provider;
    private String apiKey;
    private String model;
    private String customBaseUrl;
}