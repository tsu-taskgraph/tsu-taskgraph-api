package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class ProjectCreatedEvent extends AuditEvent {
    private final Project project;
    private final User actor;

    public ProjectCreatedEvent(Object source, Project project, User actor) {
        super(source);
        this.project = project;
        this.actor = actor;
    }
}
