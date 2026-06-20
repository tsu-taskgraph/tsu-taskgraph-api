package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;
import ru.tsu_taskgraph.core_api.entity.ProjectRole;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class MemberRoleChangedEvent extends AuditEvent {
    private final ProjectMember member;
    private final ProjectRole oldRole;
    private final User actor;

    public MemberRoleChangedEvent(Object source, ProjectMember member, ProjectRole oldRole, User actor) {
        super(source);
        this.member = member;
        this.oldRole = oldRole;
        this.actor = actor;
    }
}