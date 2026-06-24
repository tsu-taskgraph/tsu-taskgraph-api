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
public class AiMutateGraphRequest {
    private GraphSnapshot currentGraph;
    private String prompt;
    private String projectName;
    private List<String> techStack;
    private boolean aiEstimate;
    private ProviderConfig providerConfig;
}