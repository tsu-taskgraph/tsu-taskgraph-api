package ru.tsu_taskgraph.core_api.dto.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class CreateTaskRequest {
    @NotBlank
    private String title;

    private String description;

    private TaskCategory category;

    @JsonDeserialize(using = HourMinuteDeserializer.class)
    @Schema(type = "string", description = "Оценка трудозатрат. Форматы: '2.5' или '2:30'.", example = "4:00")
    private Double estimatedHours;

    private LocalDate startDate;

    private LocalDate dueDate;

    private Double positionX;

    private Double positionY;
}