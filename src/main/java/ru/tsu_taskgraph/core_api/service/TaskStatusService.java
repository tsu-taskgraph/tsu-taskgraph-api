package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;
import ru.tsu_taskgraph.core_api.mapper.TaskMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final EdgeRepository edgeRepository;
    private final TaskRepository taskRepository;
    private final TaskUtil taskUtil;
    private final TaskMapper taskMapper;
    private final GraphLayerService graphLayerService;
    private final AuditEventPublisher auditEventPublisher;

    @Transactional
    public void eventRefreshByTask(UUID taskId){
        Task task = taskUtil.getTaskById(taskId);

        auditEventPublisher.publishSystemGraphRefreshEvent(this, task);

        refreshAllTaskStatuses(task.getProject().getId());
    }

    @Transactional
    public void refreshAllTaskStatuses(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(projectId);

        // Sort tasks by layer in descending order (right to left)
        tasks.sort(Comparator.comparing(task -> layers.getOrDefault(task.getId(), 0), Comparator.reverseOrder()));

        for (Task task : tasks) {
            tryToUnlockTask(task);
            tryToLockTask(task);
        }
    }

    @Transactional
    public List<TaskNode> updateDependentTasks(UUID completedTaskId) {
        Task completedTask = taskUtil.getTaskById(completedTaskId);
        List<Edge> dependentEdges = edgeRepository.findBySourceTask(completedTask);
        List<TaskNode> unlockedTasks = new ArrayList<>();

        if (dependentEdges.isEmpty()) {
            return unlockedTasks;
        }

        // Рассчитываем слои один раз для всего проекта
        UUID projectId = completedTask.getProject().getId();
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(projectId);

        for (Edge edge : dependentEdges) {
            if (tryToUnlockTask(edge.getTargetTask())) {
                unlockedTasks.add(taskMapper.toNode(edge.getTargetTask(), layers));
            }
            tryToLockTask(edge.getTargetTask());
        }
        return unlockedTasks;
    }

    public boolean tryToUnlockTask(Task task) {
        if (task.getStatus() != TaskStatus.LOCKED) {
            return false;
        }

        List<Edge> prerequisites = edgeRepository.findByTargetTask(task);

        boolean allPrerequisitesDone = prerequisites.stream()
                .allMatch(edge -> {
                    TaskStatus status = edge.getSourceTask().getStatus();
                    return status == TaskStatus.COMPLETED || status == TaskStatus.SKIPPED;
                });

        if (allPrerequisitesDone) {
            task.setStatus(TaskStatus.AVAILABLE);
            return true;
        }
        return false;
    }

    @Transactional
    public void lockSuccessorTasks(UUID sourceTaskId) {
        Task sourceTask = taskUtil.getTaskById(sourceTaskId);
        List<Edge> successorEdges = edgeRepository.findBySourceTask(sourceTask);

        for (Edge edge : successorEdges) {
            tryToLockTask(edge.getTargetTask());
        }
    }

    public boolean tryToLockTask(Task task) {
        // We only lock tasks that are currently available
        if (task.getStatus() == TaskStatus.LOCKED) {
            return false;
        }

        List<Edge> prerequisites = edgeRepository.findByTargetTask(task);

        boolean anyPrerequisiteNotDone = prerequisites.stream()
                .anyMatch(edge -> {
                    TaskStatus status = edge.getSourceTask().getStatus();
                    return status != TaskStatus.COMPLETED && status != TaskStatus.SKIPPED;
                });

        if (anyPrerequisiteNotDone) {
            task.setStatus(TaskStatus.LOCKED);
            return true;
        }
        return false;
    }
}
