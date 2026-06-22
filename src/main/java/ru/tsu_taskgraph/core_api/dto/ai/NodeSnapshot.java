package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.util.UUID;

@Data
@Builder
public class NodeSnapshot {
    private UUID id;
    private String title;
    private TaskStatus status;
    private Double estimatedHours;
}