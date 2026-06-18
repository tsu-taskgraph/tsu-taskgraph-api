package ru.tsu_taskgraph.core_api.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;

    private Double loggedHours;

    private String comment;
}
