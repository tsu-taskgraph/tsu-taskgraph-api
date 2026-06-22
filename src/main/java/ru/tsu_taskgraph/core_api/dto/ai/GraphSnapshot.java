package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphSnapshot {
    private List<NodeSnapshot> nodes;
    private List<EdgeSnapshot> edges;
}