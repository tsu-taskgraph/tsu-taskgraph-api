package ru.tsu_taskgraph.core_api.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.dto.project.ProjectGraphResponse;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdateResponse {
    private TaskNode updatedTask;
    private List<TaskNode> unlockedTasks;
    private ProjectGraphResponse graph;
}
