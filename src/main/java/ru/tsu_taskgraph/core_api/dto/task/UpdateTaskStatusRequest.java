package ru.tsu_taskgraph.core_api.dto.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.converter.HourMinuteDeserializer;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;

    @JsonDeserialize(using = HourMinuteDeserializer.class)
    @Schema(type = "string", description = "Часы, потраченные на выполнение. Форматы: '1.5' или '1:30'.", example = "2:00")
    private Double loggedHours;

    private String comment;
}