package ru.tsu_taskgraph.core_api.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdateResponse {
    private TaskNode updatedTask;
    // TODO требуется логика задач с зависимостями
    //private List<TaskNode> unlockedTasks;
    //private ProjectGraphResponse graph;
}
