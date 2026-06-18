package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final EdgeRepository edgeRepository;
    private final TaskUtil taskUtil;

    /**
     * Обновляет статусы всех задач, которые зависят от указанной (завершенной) задачи.
     * @param completedTaskId ID задачи, которая была только что завершена или пропущена.
     */
    @Transactional
    public void updateDependentTasks(UUID completedTaskId) {
        Task completedTask = taskUtil.getTaskById(completedTaskId);
        List<Edge> dependentEdges = edgeRepository.findBySourceTask(completedTask);

        for (Edge edge : dependentEdges) {
            tryToUnlockTask(edge.getTargetTask());
        }
    }

    /**
     * Пытается разблокировать задачу, проверяя статусы всех её предшественников.
     * @param task Задача, которую нужно попытаться разблокировать.
     */
    public void tryToUnlockTask(Task task) {
        // Разблокировать можно только заблокированную задачу
        if (task.getStatus() != TaskStatus.LOCKED) {
            return;
        }

        List<Edge> prerequisites = edgeRepository.findByTargetTask(task);

        boolean allPrerequisitesDone = prerequisites.stream()
                .allMatch(edge -> {
                    TaskStatus status = edge.getSourceTask().getStatus();
                    return status == TaskStatus.COMPLETED || status == TaskStatus.SKIPPED;
                });

        if (allPrerequisitesDone) {
            task.setStatus(TaskStatus.AVAILABLE);
        }
    }
}
