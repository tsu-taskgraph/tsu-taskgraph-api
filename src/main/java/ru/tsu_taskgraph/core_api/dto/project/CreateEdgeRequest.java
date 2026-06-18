package ru.tsu_taskgraph.core_api.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEdgeRequest {

    @NotNull
    private UUID sourceTaskId;

    @NotNull
    private UUID targetTaskId;
}
