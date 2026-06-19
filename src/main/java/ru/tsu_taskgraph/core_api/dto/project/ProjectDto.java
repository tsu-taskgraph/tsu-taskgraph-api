package ru.tsu_taskgraph.core_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private UUID id;
    private Integer version;
    private String name;
    private String description;
    private List<String> techStack;
    private ProjectStatus status;
    private UUID ownerId;
    private Integer teamSize;
    private Boolean aiEstimate;
    private Double totalEstimatedHours;
    private Double totalLoggedHours;
    private Double completionPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
