package ru.tsu_taskgraph.core_api.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @Min(0)
    @Max(100)
    private Integer completionPercent;

    private LocalDate startDate;
    private LocalDate dueDate;
    private Double positionX;
    private Double positionY;

    @NotNull
    private Integer version;
}
