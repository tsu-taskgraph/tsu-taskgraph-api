package ru.tsu_taskgraph.core_api.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.TaskCategory;
import ru.tsu_taskgraph.core_api.entity.TaskEnrichment;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskNode {
    private UUID id;
    private Integer version;
    private UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskCategory category;
    private Integer layer;
    private Double positionX;
    private Double positionY;
    private List<AssigneeDto> assignees;
    private Integer completionPercent;
    private Double estimatedHours;
    private Double loggedHours;
    private LocalDate startDate;
    private LocalDate dueDate;
    private UUID wikiPageId;
    private TaskEnrichment enrichment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
