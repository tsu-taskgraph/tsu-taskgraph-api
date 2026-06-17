package ru.tsu_taskgraph.core_api.dto.task;

import jakarta.validation.constraints.NotBlank;
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
public class CreateTaskRequest {
    @NotBlank
    private String title;

    private String description;

    private TaskCategory category;

    private Double estimatedHours;

    private LocalDate startDate;

    private LocalDate dueDate;

    @Builder.Default
    private Double positionX = 0.0;

    @Builder.Default
    private Double positionY = 0.0;
}
