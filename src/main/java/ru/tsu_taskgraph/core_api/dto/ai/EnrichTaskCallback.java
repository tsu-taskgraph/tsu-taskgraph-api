package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.TaskEnrichment;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrichTaskCallback {
    private UUID jobId;
    private UUID taskId;
    private String status;
    private List<String> checklist;
    private List<String> pitfalls;
    private List<TaskEnrichment.DocLink> links;
    private String rawMarkdown;
    private String wikiDraft;
    private String error;
}