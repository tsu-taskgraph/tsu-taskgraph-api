package ru.tsu_taskgraph.core_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.repository.ProjectMemberRepository;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.Optional;
import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurityEvaluator {

    private final ProjectMemberRepository projectMemberRepository;
    private final UserUtil userUtil;

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
