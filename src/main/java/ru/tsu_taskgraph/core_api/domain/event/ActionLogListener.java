package ru.tsu_taskgraph.core_api.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.tsu_taskgraph.core_api.entity.ActionLogEntry;
import ru.tsu_taskgraph.core_api.entity.ActionLogEventType;
import ru.tsu_taskgraph.core_api.entity.AuthorType;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.repository.ActionLogRepository;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionLogListener {

    private final ActionLogRepository actionLogRepository;

    @TransactionalEventListener
    public void handleProjectCreated(ProjectCreatedEvent event) {
        var project = event.getProject();
        var actor = event.getActor();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("projectName", project.getName());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.PROJECT_CREATED)
                .description(String.format("Пользователь '%s' создал проект '%s'", actor.getDisplayName(), project.getName()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        var project = event.getProject();
        var actor = event.getActor();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("projectName", project.getName());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.PROJECT_UPDATED)
                .description(String.format("Пользователь '%s' обновил проект '%s'", actor.getDisplayName(), project.getName()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleMemberInvited(MemberInvitedEvent event) {
        var member = event.getMember();
        var actor = event.getActor();
        var project = member.getProject();
        var invitedUser = member.getUser();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("invitedUserId", invitedUser.getId());
        metadata.put("invitedUserEmail", invitedUser.getEmail());
        metadata.put("role", member.getRole());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.MEMBER_INVITED)
                .description(String.format("Пользователь '%s' пригласил '%s' в проект '%s' с ролью '%s'",
                        actor.getDisplayName(), invitedUser.getDisplayName(), project.getName(), member.getRole()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleMemberRoleChanged(MemberRoleChangedEvent event) {
        var member = event.getMember();
        var actor = event.getActor();
        var project = member.getProject();
        var targetUser = member.getUser();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("targetUserId", targetUser.getId());
        metadata.put("oldRole", event.getOldRole());
        metadata.put("newRole", member.getRole());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.MEMBER_ROLE_CHANGED)
                .description(String.format("Пользователь '%s' изменил роль '%s' с '%s' на '%s'",
                        actor.getDisplayName(), targetUser.getDisplayName(), event.getOldRole(), member.getRole()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleMemberRemoved(MemberRemovedEvent event) {
        var member = event.getMember();
        var actor = event.getActor();
        var project = member.getProject();
        var removedUser = member.getUser();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("removedUserId", removedUser.getId());
        metadata.put("removedUserEmail", removedUser.getEmail());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.MEMBER_REMOVED)
                .description(String.format("Пользователь '%s' удалил '%s' из проекта '%s'",
                        actor.getDisplayName(), removedUser.getDisplayName(), project.getName()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleTaskCreated(TaskCreatedEvent event) {
        var task = event.getTask();
        var actor = event.getActor();
        var project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.TASK_CREATED)
                .description(String.format("Пользователь '%s' создал задачу '%s'", actor.getDisplayName(), task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        var task = event.getTask();
        var actor = event.getActor();
        var project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.TASK_UPDATED)
                .description(String.format("Пользователь '%s' обновил задачу '%s'", actor.getDisplayName(), task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        var task = event.getTask();
        var actor = event.getActor();
        var project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());
        metadata.put("oldStatus", event.getOldStatus());
        metadata.put("newStatus", task.getStatus());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.TASK_STATUS_CHANGED)
                .description(String.format("Пользователь '%s' изменил статус задачи '%s' с '%s' на '%s'",
                        actor.getDisplayName(), task.getTitle(), event.getOldStatus(), task.getStatus()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {
        var task = event.getTask();
        var actor = event.getActor();
        var project = task.getProject();
        var oldAssignees = event.getOldAssignees();
        var newAssignees = task.getAssignees();

        Set<User> added = newAssignees.stream()
                .filter(u -> !oldAssignees.contains(u))
                .collect(Collectors.toSet());

        if (!added.isEmpty()) {
            String addedNames = added.stream().map(User::getDisplayName).collect(Collectors.joining(", "));
            var metadata = new HashMap<String, Object>();
            metadata.put("actorId", actor.getId());
            metadata.put("actorDisplayName", actor.getDisplayName());
            metadata.put("taskId", task.getId());
            metadata.put("taskTitle", task.getTitle());
            metadata.put("assignedUsers", added.stream().map(u -> u.getId().toString()).collect(Collectors.joining(",")));

            ActionLogEntry logEntry = ActionLogEntry.builder()
                    .project(project)
                    .actorType(AuthorType.USER)
                    .eventType(ActionLogEventType.TASK_ASSIGNED)
                    .description(String.format("Пользователь '%s' назначил '%s' на задачу '%s'",
                            actor.getDisplayName(), addedNames, task.getTitle()))
                    .metadata(metadata)
                    .build();
            actionLogRepository.save(logEntry);
        }
    }

    @TransactionalEventListener
    public void handleTaskDeleted(TaskDeletedEvent event) {
        var task = event.getTask();
        var actor = event.getActor();
        var project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.TASK_DELETED)
                .description(String.format("Пользователь '%s' удалил задачу '%s'", actor.getDisplayName(), task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleTimeLogged(TimeLoggedEvent event) {
        var timeLog = event.getTimeLog();
        var actor = event.getActor();
        var task = timeLog.getTask();
        var project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());
        metadata.put("hours", timeLog.getHours());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.TIME_LOGGED)
                .description(String.format("Пользователь '%s' залогировал %.2f ч. в задаче '%s'",
                        actor.getDisplayName(), timeLog.getHours(), task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleEdgeCreated(EdgeCreatedEvent event) {
        var edge = event.getEdge();
        var actor = event.getActor();
        var project = edge.getProject();
        var sourceTask = edge.getSourceTask();
        var targetTask = edge.getTargetTask();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("sourceTaskId", sourceTask.getId());
        metadata.put("targetTaskId", targetTask.getId());
        metadata.put("sourceTaskTitle", sourceTask.getTitle());
        metadata.put("targetTaskTitle", targetTask.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.EDGE_CREATED)
                .description(String.format("Пользователь '%s' создал зависимость от '%s' к '%s'",
                        actor.getDisplayName(), sourceTask.getTitle(), targetTask.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleEdgeDeleted(EdgeDeletedEvent event) {
        var edge = event.getEdge();
        var actor = event.getActor();
        var project = edge.getProject();
        var sourceTask = edge.getSourceTask();
        var targetTask = edge.getTargetTask();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("sourceTaskId", sourceTask.getId());
        metadata.put("targetTaskId", targetTask.getId());
        metadata.put("sourceTaskTitle", sourceTask.getTitle());
        metadata.put("targetTaskTitle", targetTask.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.EDGE_DELETED)
                .description(String.format("Пользователь '%s' удалил зависимость от '%s' к '%s'",
                        actor.getDisplayName(), sourceTask.getTitle(), targetTask.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleWikiPageCreated(WikiPageCreatedEvent event) {
        var wikiPage = event.getWikiPage();
        var actor = event.getActor();
        var project = wikiPage.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("wikiPageId", wikiPage.getId());
        metadata.put("wikiPageTitle", wikiPage.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.WIKI_PAGE_CREATED)
                .description(String.format("Пользователь '%s' создал Wiki-страницу '%s'", actor.getDisplayName(), wikiPage.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleWikiPageUpdated(WikiPageUpdatedEvent event) {
        var wikiPage = event.getWikiPage();
        var actor = event.getActor();
        var project = wikiPage.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("actorId", actor.getId());
        metadata.put("actorDisplayName", actor.getDisplayName());
        metadata.put("wikiPageId", wikiPage.getId());
        metadata.put("wikiPageTitle", wikiPage.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.USER)
                .eventType(ActionLogEventType.WIKI_PAGE_UPDATED)
                .description(String.format("Пользователь '%s' обновил Wiki-страницу '%s'", actor.getDisplayName(), wikiPage.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleAiSkeletonGenerated(AiSkeletonGeneratedEvent event) {
        var project = event.getProject();
        var response = event.getResponse();

        var metadata = new HashMap<String, Object>();
        metadata.put("modelUsed", response.getModelUsed());
        metadata.put("provider", response.getProvider());
        metadata.put("promptTokens", response.getPromptTokens());
        metadata.put("completionTokens", response.getCompletionTokens());
        metadata.put("thinkingTokens", response.getThinkingTokens());
        metadata.put("totalEstimatedHours", response.getTotalEstimatedHours());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.AI)
                .eventType(ActionLogEventType.AI_SKELETON_GENERATED)
                .description(String.format("ИИ сгенерировал скелет проекта: %d задач, %d зависимостей.",
                        response.getNodes().size(), response.getEdges().size()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }
}