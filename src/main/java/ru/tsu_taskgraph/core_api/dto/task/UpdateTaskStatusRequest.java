package ru.tsu_taskgraph.core_api.dto.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.converter.HourMinuteDeserializer;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.time.LocalDateTime;

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

    @PastOrPresent(message = "Дата лога не может быть в будущем")
    @Schema(description = "Дата и время лога. Если не указано, используется текущее время.", example = "2024-05-20T13:45:00")
    private LocalDateTime loggedAt;
}
