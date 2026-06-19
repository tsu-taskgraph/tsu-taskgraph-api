package ru.tsu_taskgraph.core_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.repository.ProjectMemberRepository;
import ru.tsu_taskgraph.core_api.util.*;

import java.util.Optional;
import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurityEvaluator {

    private final ProjectMemberRepository projectMemberRepository;
    private final EdgeUtil edgeUtil;
    private final TaskUtil taskUtil;
    private final TimeLogUtil timeLogUtil;
    private final UserUtil userUtil;
    private final WikiPageUtil wikiPageUtil;

    public boolean isOwner(UUID projectId) {
        return hasRole(projectId, ProjectRole.OWNER);
    }

    public boolean isAdmin(UUID projectId) {
        return hasRole(projectId, ProjectRole.ADMIN);
    }

    public boolean isMember(UUID projectId) {
        return hasRole(projectId, ProjectRole.MEMBER);
    }

    public boolean isViewer(UUID projectId) {
        return hasRole(projectId, ProjectRole.VIEWER);
    }

    public boolean canAccessTask(UUID taskId, String requiredRole) {
        ProjectRole role = ProjectRole.valueOf(requiredRole);
        Task task = taskUtil.getTaskById(taskId);
        return hasRole(task.getProject().getId(), role);
    }

    public boolean canAccessEdge(UUID edgeId, String requiredRole) {
        ProjectRole role = ProjectRole.valueOf(requiredRole);
        Edge edge = edgeUtil.getEdgeById(edgeId);
        return hasRole(edge.getProject().getId(), role);
    }

    public boolean canAccessWikiPage(UUID pageId, String requiredRole) {
        ProjectRole role = ProjectRole.valueOf(requiredRole);
        WikiPage page = wikiPageUtil.getWikiPageById(pageId);
        return hasRole(page.getProject().getId(), role);
    }

    public boolean canDeleteTimeLog(UUID logId, UUID taskId) {
        User currentUser = userUtil.getCurrentUserFromContext();
        TimeLog timeLog = timeLogUtil.getTimeLogById(logId);
        if (!timeLog.getTask().getId().equals(taskId)) {
            return false;
        }
        if (timeLog.getUser().getId().equals(currentUser.getId())) {
            return true;
        }
        return hasRole(timeLog.getTask().getProject().getId(), ProjectRole.ADMIN);
    }

    private boolean hasRole(UUID projectId, ProjectRole requiredRole) {
        try {
            User currentUser = userUtil.getCurrentUserFromContext();
            Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectIdAndUserId(projectId, currentUser.getId());

            return memberOpt
                    .map(member -> member.getRole().ordinal() <= requiredRole.ordinal())
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
