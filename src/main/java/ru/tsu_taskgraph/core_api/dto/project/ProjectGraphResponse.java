package ru.tsu_taskgraph.core_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.entity.EnrichmentStatus;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectGraphResponse {
    private UUID projectId;
    private List<TaskNode> nodes;
    private List<EdgeResponse> edges;
    private EnrichmentStatus enrichmentStatus;
}
