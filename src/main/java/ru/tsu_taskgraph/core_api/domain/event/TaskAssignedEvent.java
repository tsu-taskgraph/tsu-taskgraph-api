package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.User;

import java.util.Set;

@Getter
public class TaskAssignedEvent extends AuditEvent {
    private final Task task;
    private final Set<User> oldAssignees;
    private final User actor;

    public TaskAssignedEvent(Object source, Task task, Set<User> oldAssignees, User actor) {
        super(source);
        this.task = task;
        this.oldAssignees = oldAssignees;
        this.actor = actor;
    }
}