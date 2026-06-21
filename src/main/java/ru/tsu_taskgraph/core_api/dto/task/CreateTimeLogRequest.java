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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTimeLogRequest {
    @NotNull
    @JsonDeserialize(using = HourMinuteDeserializer.class)
    @Schema(type = "string", description = "Затраченное время. Форматы: '1.5' (полтора часа) или '1:30' (1 час 30 минут).", example = "1:30")
    private Double hours;

    private String comment;

    @PastOrPresent(message = "Дата лога не может быть в будущем")
    @Schema(description = "Дата и время лога. Если не указано, используется текущее время.", example = "2024-05-20T13:45:00")
    private LocalDateTime loggedAt;
}
