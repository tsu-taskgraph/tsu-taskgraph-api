package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.ProjectMemberRepository;
import ru.tsu_taskgraph.core_api.repository.ProjectRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectUtil {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * Получает проект по его ID из базы данных.
     *
     * @param id UUID проекта
     * @return {@link Project} из базы данных
     * @throws ResourceNotFoundException если проект не найден
     */
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Проект с ID " + id + " не найден"));
    }

    /**
     * Получает участника проекта по проекту и пользователю.
     *
     * @param project Проект
     * @param user    Пользователь
     * @return {@link ProjectMember} из базы данных
     * @throws ResourceNotFoundException если участник не найден
     */
    public ProjectMember getProjectMember(Project project, User user) {
        return projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new ResourceNotFoundException("Участник с ID " + user.getId() + " не найден в проекте с ID " + project.getId()));
    }
}
