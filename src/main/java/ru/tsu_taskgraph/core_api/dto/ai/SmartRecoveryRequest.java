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
public class SmartRecoveryRequest {
    private GraphSnapshot currentGraph;
    private MutationPatch failedMutation;
    private List<String> cycleNodes;
    private String projectName;
    private List<String> techStack;
    private Boolean aiEstimate;
    private ProviderConfig providerConfig;
}