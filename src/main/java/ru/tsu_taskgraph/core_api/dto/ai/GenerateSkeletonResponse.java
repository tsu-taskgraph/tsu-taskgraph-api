package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.tsu_taskgraph.core_api.entity.AiProvider;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateSkeletonResponse {
    private List<SkeletonNode> nodes;
    private List<SkeletonEdge> edges;
    private Double totalEstimatedHours;
    private String modelUsed;
    private AiProvider provider;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer thinkingTokens;
}