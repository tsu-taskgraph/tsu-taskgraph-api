package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.tsu_taskgraph.core_api.entity.TaskCategory;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkeletonNode {
    private String tempId;
    private String title;
    private String description;
    private TaskCategory category;
    private Double estimatedHours;
}