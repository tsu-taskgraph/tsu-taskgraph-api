package ru.tsu_taskgraph.core_api.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTimeLogRequest {
    @NotNull
    @Min(0.25)
    private Double hours;
    private String comment;
}
