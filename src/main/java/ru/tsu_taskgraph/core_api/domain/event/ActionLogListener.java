package ru.tsu_taskgraph.core_api.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.repository.ActionLogRepository;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.TaskUtil;
import ru.tsu_taskgraph.core_api.util.UserUtil;
import ru.tsu_taskgraph.core_api.util.WikiPageUtil;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionLogListener {

    private final ActionLogRepository actionLogRepository;
    private final TaskUtil taskUtil;
    private final ProjectUtil projectUtil;
    private final UserUtil userUtil;
    private final WikiPageUtil wikiPageUtil;

    @TransactionalEventListener
    public void handleProjectCreated(ProjectCreatedEvent event) {
        Project project = projectUtil.getProjectById(event.getProject().getId());
        User actor = userUtil.getUserById(event.getActor().getId());

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
        Project project = projectUtil.getProjectById(event.getProject().getId());
        User actor = userUtil.getUserById(event.getActor().getId());

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
        ProjectMember member = event.getMember();
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(member.getProject().getId());
        User invitedUser = userUtil.getUserById(member.getUser().getId());

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
        ProjectMember member = event.getMember();
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(member.getProject().getId());
        User targetUser = userUtil.getUserById(member.getUser().getId());

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

    @EventListener
    public void handleMemberRemoved(MemberRemovedEvent event) {
        ProjectMember member = event.getMember();
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(member.getProject().getId());
        User removedUser = userUtil.getUserById(member.getUser().getId());

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
        Task task = taskUtil.getTaskById(event.getTask().getId());
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = task.getProject();

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
        Task task = taskUtil.getTaskById(event.getTask().getId());
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = task.getProject();

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
        Task task = taskUtil.getTaskById(event.getTask().getId());
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = task.getProject();

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
        Task task = taskUtil.getTaskById(event.getTask().getId());
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = task.getProject();
        Set<User> oldAssignees = event.getOldAssignees();
        Set<User> newAssignees = task.getAssignees();

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

    @EventListener
    public void handleTaskDeleted(TaskDeletedEvent event) {
        Task task = event.getTask(); // This is a detached, deleted entity
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(task.getProject().getId());

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
        TimeLog timeLog = event.getTimeLog();
        User actor = userUtil.getUserById(event.getActor().getId());
        Task task = taskUtil.getTaskById(timeLog.getTask().getId());
        Project project = task.getProject();

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
        Edge edge = event.getEdge();
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(edge.getProject().getId());
        Task sourceTask = taskUtil.getTaskById(edge.getSourceTask().getId());
        Task targetTask = taskUtil.getTaskById(edge.getTargetTask().getId());

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

    @EventListener
    public void handleEdgeDeleted(EdgeDeletedEvent event) {
        Edge edge = event.getEdge(); // This is a detached, deleted entity
        User actor = userUtil.getUserById(event.getActor().getId());
        Project project = projectUtil.getProjectById(edge.getProject().getId());
        Task sourceTask = taskUtil.getTaskById(edge.getSourceTask().getId());
        Task targetTask = taskUtil.getTaskById(edge.getTargetTask().getId());

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
        WikiPage wikiPage = wikiPageUtil.getWikiPageById(event.getWikiPage().getId());
        User actor = event.getActor() != null ? userUtil.getUserById(event.getActor().getId()) : null;
        Project project = wikiPage.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("wikiPageId", wikiPage.getId());
        metadata.put("wikiPageTitle", wikiPage.getTitle());

        ActionLogEntry.ActionLogEntryBuilder logEntryBuilder = ActionLogEntry.builder()
                .project(project)
                .eventType(ActionLogEventType.WIKI_PAGE_CREATED)
                .metadata(metadata);

        if (actor != null) {
            metadata.put("actorId", actor.getId());
            metadata.put("actorDisplayName", actor.getDisplayName());
            logEntryBuilder
                    .actorType(AuthorType.USER)
                    .description(String.format("Пользователь '%s' создал Wiki-страницу '%s'", actor.getDisplayName(), wikiPage.getTitle()));
        } else {
            metadata.put("actorDisplayName", AuthorType.AI_DISPLAY_NAME);
            logEntryBuilder
                    .actorType(AuthorType.AI)
                    .description(String.format("ИИ создал Wiki-страницу '%s'", wikiPage.getTitle()));
        }

        actionLogRepository.save(logEntryBuilder.build());
    }

    @TransactionalEventListener
    public void handleWikiPageUpdated(WikiPageUpdatedEvent event) {
        WikiPage wikiPage = wikiPageUtil.getWikiPageById(event.getWikiPage().getId());
        User actor = event.getActor() != null ? userUtil.getUserById(event.getActor().getId()) : null;
        Project project = wikiPage.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("wikiPageId", wikiPage.getId());
        metadata.put("wikiPageTitle", wikiPage.getTitle());

        ActionLogEntry.ActionLogEntryBuilder logEntryBuilder = ActionLogEntry.builder()
                .project(project)
                .eventType(ActionLogEventType.WIKI_PAGE_UPDATED)
                .metadata(metadata);

        if (actor != null) {
            metadata.put("actorId", actor.getId());
            metadata.put("actorDisplayName", actor.getDisplayName());
            logEntryBuilder
                    .actorType(AuthorType.USER)
                    .description(String.format("Пользователь '%s' обновил Wiki-страницу '%s'", actor.getDisplayName(), wikiPage.getTitle()));
        } else {
            metadata.put("actorDisplayName", AuthorType.AI_DISPLAY_NAME);
            logEntryBuilder
                    .actorType(AuthorType.AI)
                    .description(String.format("ИИ обновил Wiki-страницу '%s'", wikiPage.getTitle()));
        }

        actionLogRepository.save(logEntryBuilder.build());
    }

    @TransactionalEventListener
    public void handleAiSkeletonGenerated(AiSkeletonGeneratedEvent event) {
        Project project = projectUtil.getProjectById(event.getProject().getId());
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

    @TransactionalEventListener
    public void handleAiEnrichmentCompleted(AiEnrichmentCompletedEvent event) {
        Task task = taskUtil.getTaskById(event.getTask().getId());
        Project project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.AI)
                .eventType(ActionLogEventType.AI_ENRICHMENT_COMPLETED)
                .description(String.format("ИИ успешно обогатил задачу '%s'", task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }

    @TransactionalEventListener
    public void handleAiEnrichmentFailed(AiEnrichmentFailedEvent event) {
        Task task = taskUtil.getTaskById(event.getTask().getId());
        Project project = task.getProject();

        var metadata = new HashMap<String, Object>();
        metadata.put("taskId", task.getId());
        metadata.put("taskTitle", task.getTitle());
        metadata.put("error", event.getError());

        ActionLogEntry logEntry = ActionLogEntry.builder()
                .project(project)
                .actorType(AuthorType.AI)
                .eventType(ActionLogEventType.AI_ENRICHMENT_FAILED)
                .description(String.format("ИИ не смог обогатить задачу '%s'", task.getTitle()))
                .metadata(metadata)
                .build();

        actionLogRepository.save(logEntry);
    }
}
