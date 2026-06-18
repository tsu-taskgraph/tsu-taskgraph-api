package ru.tsu_taskgraph.core_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectMetricsDto {
    private Double totalEstimatedHours;
    private Double totalLoggedHours;
    private Double completionPercent;
}
