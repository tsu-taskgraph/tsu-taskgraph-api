package ru.tsu_taskgraph.core_api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEnrichment {
    private List<String> checklist;
    private List<String> pitfalls;
    private List<DocLink> links;
    private String rawMarkdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocLink {
        private String title;
        private String url;
    }
}
