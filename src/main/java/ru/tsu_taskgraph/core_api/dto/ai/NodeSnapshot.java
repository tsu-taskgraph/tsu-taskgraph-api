package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeSnapshot {
    private UUID id;
    private String title;
    private TaskStatus status;
    private Double estimatedHours;
}