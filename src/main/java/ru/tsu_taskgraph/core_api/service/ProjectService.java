package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.ai.AiRequestConfig;
import ru.tsu_taskgraph.core_api.dto.project.*;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.exception.ResourceConflictException;
import ru.tsu_taskgraph.core_api.mapper.ProjectMapper;
import ru.tsu_taskgraph.core_api.repository.ProjectMemberRepository;
import ru.tsu_taskgraph.core_api.repository.ProjectRepository;
import ru.tsu_taskgraph.core_api.repository.specification.ProjectSpecification;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;
    private final UserUtil userUtil;
    private final ProjectUtil projectUtil;
    private final AuditEventPublisher auditEventPublisher;
    private final AiService aiService;

    @Transactional
    public ProjectDto createProject(CreateProjectRequest request, UUID ownerId, AiRequestConfig aiConfig) {
        User owner = userUtil.getUserById(ownerId);

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .status(ProjectStatus.PENDING_AI)
                .owner(owner)
                .teamSize(1)
                .version(1)
                .aiEstimate(request.getAiEstimate() != null ? request.getAiEstimate() : true)
                .build();

        project = projectRepository.save(project);

        ProjectMember ownerMember = ProjectMember.builder()
                .project(project)
                .user(owner)
                .role(ProjectRole.OWNER)
                .build();
        project.getMembers().add(ownerMember);

        project = projectRepository.save(project);

        auditEventPublisher.publishProjectCreatedEvent(this, project, owner);

        aiService.generateSkeletonForProject(project, aiConfig);

        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getUserProjects(UUID userId, ProjectStatus status, String name, Pageable pageable) {
        Specification<Project> spec = Specification.where(ProjectSpecification.userIsMemberOrOwner(userId));

        if (status != null) {
            spec = spec.and(ProjectSpecification.statusEquals(status));
        }
        if (name != null && !name.isBlank()) {
            spec = spec.and(ProjectSpecification.nameContains(name));
        }

        return projectRepository.findAll(spec, pageable)
                .map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID projectId) {
        Project project = projectUtil.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto updateProject(UUID projectId, UpdateProjectRequest request) {
        Project project = projectUtil.getProjectById(projectId);
        User currentUser = userUtil.getCurrentUserFromContext();

        if (!Objects.equals(request.getVersion(), project.getVersion())) {
            throw new ResourceConflictException("Проект был изменен другим пользователем. Пожалуйста, обновите страницу.");
        }

        projectMapper.updateProjectFromDto(request, project);

        project = projectRepository.save(project);

        auditEventPublisher.publishProjectUpdatedEvent(this, project, currentUser);

        return projectMapper.toDto(project);
    }

    @Transactional
    public void deleteProject(UUID projectId) {
        projectRepository.delete(projectUtil.getProjectById(projectId));
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberDto> listProjectMembers(UUID projectId) {
        Project project = projectUtil.getProjectById(projectId);
        return projectMemberRepository.findByProject(project).stream()
                .map(projectMapper::toMemberDto)
                .toList();
    }

    @Transactional
    public ProjectMemberDto inviteMember(UUID projectId, InviteMemberRequest request) {
        Project project = projectUtil.getProjectById(projectId);
        User userToInvite = userUtil.getUserByEmail(request.getEmail());
        User currentUser = userUtil.getCurrentUserFromContext();

        if (projectMemberRepository.existsByProjectAndUser(project, userToInvite)) {
            throw new ResourceConflictException("Пользователь уже является участником проекта");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(userToInvite)
                .role(request.getRole())
                .build();

        member = projectMemberRepository.save(member);

        // Обновляем размер команды
        project.setTeamSize(project.getTeamSize() + 1);
        projectRepository.save(project);

        auditEventPublisher.publishMemberInvitedEvent(this, member, currentUser);

        return projectMapper.toMemberDto(member);
    }

    @Transactional
    public ProjectMemberDto updateMemberRole(UUID projectId, UUID userId, UpdateMemberRoleRequest request) {
        Project project = projectUtil.getProjectById(projectId);
        User user = userUtil.getUserById(userId);
        User currentUser = userUtil.getCurrentUserFromContext();

        ProjectMember member = projectUtil.getProjectMember(project, user);
        ProjectRole oldRole = member.getRole();

        member.setRole(request.getRole());
        member = projectMemberRepository.save(member);

        auditEventPublisher.publishMemberRoleChangedEvent(this, member, oldRole, currentUser);

        return projectMapper.toMemberDto(member);
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        Project project = projectUtil.getProjectById(projectId);
        User user = userUtil.getUserById(userId);
        User currentUser = userUtil.getCurrentUserFromContext();

        ProjectMember member = projectUtil.getProjectMember(project, user);

        projectMemberRepository.delete(member);

        // Обновляем размер команды
        project.setTeamSize(Math.max(1, project.getTeamSize() - 1));
        projectRepository.save(project);

        auditEventPublisher.publishMemberRemovedEvent(this, member, currentUser);
    }
}