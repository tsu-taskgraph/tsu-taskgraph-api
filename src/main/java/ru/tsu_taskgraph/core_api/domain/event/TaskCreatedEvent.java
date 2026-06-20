package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class TaskCreatedEvent extends AuditEvent {
    private final Task task;
    private final User actor;

    public TaskCreatedEvent(Object source, Task task, User actor) {
        super(source);
        this.task = task;
        this.actor = actor;
    }
}