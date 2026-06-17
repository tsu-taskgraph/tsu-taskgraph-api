package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.ProjectRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectUtil {

    private final ProjectRepository projectRepository;

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
}
