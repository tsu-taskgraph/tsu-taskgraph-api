package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrichTaskJobResponse {
    private UUID jobId;
    private UUID taskId;
    private String status;
    private Integer estimatedSeconds;
}