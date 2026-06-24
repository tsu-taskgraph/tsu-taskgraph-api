package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphSnapshot {
    private List<NodeSnapshot> nodes;
    private List<EdgeSnapshot> edges;
}