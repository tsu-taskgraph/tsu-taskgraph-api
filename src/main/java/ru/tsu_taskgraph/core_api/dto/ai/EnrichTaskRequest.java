package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnrichTaskRequest {
    private String projectName;
    private List<String> techStack;
    private TaskContext task;
    private String callbackUrl;
    private boolean generateWikiDraft;
    private ProviderConfig providerConfig;
}