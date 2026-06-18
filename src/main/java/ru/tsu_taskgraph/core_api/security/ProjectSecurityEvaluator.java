package ru.tsu_taskgraph.core_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;
import ru.tsu_taskgraph.core_api.entity.ProjectRole;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.repository.ProjectMemberRepository;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.Optional;
import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurityEvaluator {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectUtil projectUtil;
    private final UserUtil userUtil;

    public boolean isOwner(UUID projectId) {
        return hasAnyRole(projectId, ProjectRole.OWNER);
    }

    public boolean isOwnerOrAdmin(UUID projectId) {
        return hasAnyRole(projectId, ProjectRole.OWNER, ProjectRole.ADMIN);
    }

    public boolean isMember(UUID projectId) {
        return hasAnyRole(projectId, ProjectRole.OWNER, ProjectRole.ADMIN, ProjectRole.MEMBER);
    }

    public boolean hasAccess(UUID projectId) {
        return hasAnyRole(projectId, ProjectRole.OWNER, ProjectRole.ADMIN, ProjectRole.MEMBER, ProjectRole.VIEWER);
    }

    private boolean hasAnyRole(UUID projectId, ProjectRole... allowedRoles) {
        try {
            User currentUser = userUtil.getCurrentUserFromContext();
            Project project = projectUtil.getProjectById(projectId);

            Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectAndUser(project, currentUser);

            if (memberOpt.isEmpty()) {
                return false;
            }

            ProjectRole userRole = memberOpt.get().getRole();
            for (ProjectRole allowedRole : allowedRoles) {
                if (userRole == allowedRole) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}