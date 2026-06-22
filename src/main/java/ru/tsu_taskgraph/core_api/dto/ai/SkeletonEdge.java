package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkeletonEdge {
    private String sourceTempId;
    private String targetTempId;
}