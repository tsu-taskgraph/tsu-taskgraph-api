package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MutationPatch {
    private List<SkeletonNode> newNodes;
    private List<SkeletonEdge> newEdges;
    private Double recalculatedTotalHours;
    private String reasoning;
}