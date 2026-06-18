package ru.tsu_taskgraph.core_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdgeResponse {
    private UUID id;
    private UUID sourceTaskId;
    private UUID targetTaskId;
}
