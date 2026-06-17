package ru.tsu_taskgraph.core_api.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.TaskCategory;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskCategory category;
    private Double estimatedHours;
    private Integer completionPercent;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Double positionX;
    private Double positionY;
}
