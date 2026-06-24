package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateSkeletonRequest {
    private String projectName;
    private List<String> techStack;
    private String description;
    private Integer teamSize;
    private Boolean aiEstimate;
    private ProviderConfig providerConfig;
}