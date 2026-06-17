package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.project.CreateProjectRequest;
import ru.tsu_taskgraph.core_api.dto.project.ProjectDto;
import ru.tsu_taskgraph.core_api.dto.project.UpdateProjectRequest;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.mapper.ProjectMapper;
import ru.tsu_taskgraph.core_api.repository.ProjectRepository;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;
    private final UserUtil userUtil;
    private final ProjectUtil projectUtil;

    @Transactional
    public ProjectDto createProject(CreateProjectRequest request, UUID ownerId) {
        User owner = userUtil.getUserById(ownerId);

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .status(ProjectStatus.ACTIVE)
                .owner(owner)
                .teamSize(1)
                .aiEstimate(request.getAiEstimate() != null ? request.getAiEstimate() : true)
                .build();

        project = projectRepository.save(project);

        //TODO добавление owner в проект

        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getUserProjects(UUID userId, Pageable pageable) {
        return projectRepository.findAllUserProjects(userId, pageable)
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

        projectMapper.updateProjectFromDto(request, project);

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Transactional
    public void deleteProject(UUID projectId) {
        projectRepository.delete(projectUtil.getProjectById(projectId));
    }
}
