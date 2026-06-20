package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class MemberInvitedEvent extends AuditEvent {
    private final ProjectMember member;
    private final User actor;

    public MemberInvitedEvent(Object source, ProjectMember member, User actor) {
        super(source);
        this.member = member;
        this.actor = actor;
    }
}