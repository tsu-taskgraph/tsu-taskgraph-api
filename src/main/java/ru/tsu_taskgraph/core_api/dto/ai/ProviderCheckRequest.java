package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderCheckRequest {
    private ProviderConfig providerConfig;
}