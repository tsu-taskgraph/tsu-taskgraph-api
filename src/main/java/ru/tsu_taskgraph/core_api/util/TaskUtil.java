package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskUtil {

    private final TaskRepository taskRepository;

    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id=" + taskId + " не найдена"));
    }
}