package ru.tsu_taskgraph.core_api.dto.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.converter.HourMinuteDeserializer;
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

    @JsonDeserialize(using = HourMinuteDeserializer.class)
    @Schema(type = "string", description = "Оценка трудозатрат. Форматы: '1.5' или '1:30'.", example = "1:00")
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