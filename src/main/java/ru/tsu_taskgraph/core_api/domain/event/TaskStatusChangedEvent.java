package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class TaskStatusChangedEvent extends AuditEvent {
    private final Task task;
    private final TaskStatus oldStatus;
    private final User actor;

    public TaskStatusChangedEvent(Object source, Task task, TaskStatus oldStatus, User actor) {
        super(source);
        this.task = task;
        this.oldStatus = oldStatus;
        this.actor = actor;
    }
}