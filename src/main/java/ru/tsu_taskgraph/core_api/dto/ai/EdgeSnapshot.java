package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EdgeSnapshot {
    private UUID sourceTaskId;
    private UUID targetTaskId;
}