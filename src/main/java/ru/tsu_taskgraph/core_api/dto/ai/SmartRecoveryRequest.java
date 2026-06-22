package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SmartRecoveryRequest {
    private GraphSnapshot currentGraph;
    private MutationPatch failedMutation;
    private List<String> cycleNodes;
    private String projectName;
    private List<String> techStack;
    private Boolean aiEstimate;
    private ProviderConfig providerConfig;
}