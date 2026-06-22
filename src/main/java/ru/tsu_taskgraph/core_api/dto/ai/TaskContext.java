package ru.tsu_taskgraph.core_api.dto.ai;

import lombok.Builder;
import lombok.Data;
import ru.tsu_taskgraph.core_api.entity.TaskCategory;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TaskContext {
    private UUID taskId;
    private String taskTitle;
    private String taskDescription;
    private TaskCategory category;
    private List<String> predecessorTitles;
    private List<String> successorTitles;
    private Double estimatedHours;
}