package ru.tsu_taskgraph.core_api.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.tsu_taskgraph.core_api.entity.*;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishProjectCreatedEvent(Object source, Project project, User actor) {
        publisher.publishEvent(new ProjectCreatedEvent(source, project, actor));
    }

    public void publishProjectUpdatedEvent(Object source, Project project, User actor) {
        publisher.publishEvent(new ProjectUpdatedEvent(source, project, actor));
    }

    public void publishMemberInvitedEvent(Object source, ProjectMember member, User actor) {
        publisher.publishEvent(new MemberInvitedEvent(source, member, actor));
    }

    public void publishMemberRoleChangedEvent(Object source, ProjectMember member, ProjectRole oldRole, User actor) {
        publisher.publishEvent(new MemberRoleChangedEvent(source, member, oldRole, actor));
    }

    public void publishMemberRemovedEvent(Object source, ProjectMember member, User actor) {
        publisher.publishEvent(new MemberRemovedEvent(source, member, actor));
    }

    public void publishTaskCreatedEvent(Object source, Task task, User actor) {
        publisher.publishEvent(new TaskCreatedEvent(source, task, actor));
    }

    public void publishTaskUpdatedEvent(Object source, Task task, User actor) {
        publisher.publishEvent(new TaskUpdatedEvent(source, task, actor));
    }

    public void publishTaskStatusChangedEvent(Object source, Task task, TaskStatus oldStatus, User actor) {
        publisher.publishEvent(new TaskStatusChangedEvent(source, task, oldStatus, actor));
    }

    public void publishTaskAssignedEvent(Object source, Task task, Set<User> oldAssignees, User actor) {
        publisher.publishEvent(new TaskAssignedEvent(source, task, oldAssignees, actor));
    }

    public void publishTaskDeletedEvent(Object source, Task task, User actor) {
        publisher.publishEvent(new TaskDeletedEvent(source, task, actor));
    }

    public void publishTimeLoggedEvent(Object source, TimeLog timeLog, User actor) {
        publisher.publishEvent(new TimeLoggedEvent(source, timeLog, actor));
    }

    public void publishEdgeCreatedEvent(Object source, Edge edge, User actor) {
        publisher.publishEvent(new EdgeCreatedEvent(source, edge, actor));
    }

    public void publishEdgeDeletedEvent(Object source, Edge edge, User actor) {
        publisher.publishEvent(new EdgeDeletedEvent(source, edge, actor));
    }

    public void publishWikiPageCreatedEvent(Object source, WikiPage wikiPage, User actor) {
        publisher.publishEvent(new WikiPageCreatedEvent(source, wikiPage, actor));
    }

    public void publishWikiPageUpdatedEvent(Object source, WikiPage wikiPage, User actor) {
        publisher.publishEvent(new WikiPageUpdatedEvent(source, wikiPage, actor));
    }
}