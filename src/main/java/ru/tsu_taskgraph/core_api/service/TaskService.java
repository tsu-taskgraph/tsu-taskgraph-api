package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.task.*;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.exception.ResourceConflictException;
import ru.tsu_taskgraph.core_api.mapper.TaskMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.repository.UserRepository;
import ru.tsu_taskgraph.core_api.repository.specification.TaskSpecification;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EdgeRepository edgeRepository;
    private final TaskMapper taskMapper;
    private final TimeLogService timeLogService;
    private final TaskStatusService taskStatusService;
    private final GraphLayerService graphLayerService;
    private final ProjectGraphService projectGraphService;
    private final ProjectUtil projectUtil;
    private final TaskUtil taskUtil;

    @Transactional
    public TaskNode createTask(UUID projectId, CreateTaskRequest request) {
        Project project = projectUtil.getProjectById(projectId);

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .estimatedHours(request.getEstimatedHours())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .positionX(request.getPositionX())
                .positionY(request.getPositionY())
                .version(1)
                .build();
        task = taskRepository.save(task);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(projectId);
        return taskMapper.toNode(task, layers);
    }

    @Transactional(readOnly = true)
    public TaskNode getTask(UUID taskId) {
        Task task = taskUtil.getTaskById(taskId);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(task.getProject().getId());
        return taskMapper.toNode(task, layers);
    }

    @Transactional(readOnly = true)
    public List<TaskNode> getTasksByProject(UUID projectId, TaskStatus status, UUID assigneeId, TaskCategory category) {
        Specification<Task> spec = Specification.where(TaskSpecification.projectIdEquals(projectId));

        spec = spec.and(status != null ? TaskSpecification.statusEquals(status) : null)
                .and(assigneeId != null ? TaskSpecification.assigneeIdEquals(assigneeId) : null)
                .and(category != null ? TaskSpecification.categoryEquals(category) : null);

        List<Task> tasks = taskRepository.findAll(spec);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(projectId);
        return taskMapper.toNodeList(tasks, layers);
    }

    @Transactional
    public TaskNode updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskUtil.getTaskById(taskId);

        if (!Objects.equals(request.getVersion(), task.getVersion())) {
            throw new ResourceConflictException("Задача была изменена другим пользователем. Пожалуйста, обновите страницу.");
        }

        taskMapper.updateFromRequest(request, task);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(task.getProject().getId());
        return taskMapper.toNode(task, layers);
    }

    @Transactional
    public TaskStatusUpdateResponse updateTaskStatus(UUID taskId, UpdateTaskStatusRequest request, User currentUser) {
        Task task = taskUtil.getTaskById(taskId);
        TaskStatus oldStatus = task.getStatus();
        task.setStatus(request.getStatus());

        if (request.getLoggedHours() != null && request.getLoggedHours() > 0) {
            timeLogService.createTimeLog(task.getId(), new CreateTimeLogRequest(request.getLoggedHours(), request.getComment()), currentUser);
        }

        List<TaskNode> unlockedTasks = new ArrayList<>();
        if ((task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.SKIPPED) && oldStatus != task.getStatus()) {
            unlockedTasks = taskStatusService.updateDependentTasks(task.getId());
        }

        Map<UUID, Integer> layers = graphLayerService.calculateLayers(task.getProject().getId());
        return TaskStatusUpdateResponse.builder()
                .updatedTask(taskMapper.toNode(task, layers))
                .unlockedTasks(unlockedTasks)
                .graph(projectGraphService.getProjectGraph(task.getProject().getId()))
                .build();
    }

    @Transactional
    public TaskNode assignTask(UUID taskId, AssignTaskRequest request) {
        Task task = taskUtil.getTaskById(taskId);
        Set<User> assignees = new HashSet<>(userRepository.findAllById(request.getUserIds()));
        task.setAssignees(assignees);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(task.getProject().getId());
        return taskMapper.toNode(task, layers);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        Task taskToDelete = taskUtil.getTaskById(taskId);

        if (edgeRepository.existsBySourceTask(taskToDelete)) {
            throw new BadRequestException("Нельзя удалить задачу, от которой зависят другие задачи.");
        }

        List<Edge> parentEdges = edgeRepository.findByTargetTask(taskToDelete);
        List<UUID> parentIds = parentEdges.stream().map(edge -> edge.getSourceTask().getId()).toList();

        taskRepository.delete(taskToDelete);

        parentIds.forEach(taskStatusService::updateDependentTasks);
    }
}
