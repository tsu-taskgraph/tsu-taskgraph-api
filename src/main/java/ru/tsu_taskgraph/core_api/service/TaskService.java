package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.project.ProjectGraphResponse;
import ru.tsu_taskgraph.core_api.dto.task.*;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.mapper.TaskMapper;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.repository.UserRepository;
import ru.tsu_taskgraph.core_api.repository.specification.TaskSpecification;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TimeLogService timeLogService;
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
                .build();
        task = taskRepository.save(task);
        return taskMapper.toNode(task);
    }

    @Transactional(readOnly = true)
    public TaskNode getTask(UUID taskId) {
        Task task = taskUtil.getTaskById(taskId);
        return taskMapper.toNode(task);
    }

    @Transactional(readOnly = true)
    public List<TaskNode> getTasksByProject(UUID projectId, TaskStatus status, UUID assigneeId, TaskCategory category) {
        Specification<Task> spec = Specification.where(TaskSpecification.projectIdEquals(projectId));

        spec = spec.and(status != null ? TaskSpecification.statusEquals(status) : null)
                .and(assigneeId != null ? TaskSpecification.assigneeIdEquals(assigneeId) : null)
                .and(category != null ? TaskSpecification.categoryEquals(category) : null);

        List<Task> tasks = taskRepository.findAll(spec);
        return taskMapper.toNodeList(tasks);
    }

    @Transactional
    public TaskNode updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskUtil.getTaskById(taskId);
        taskMapper.updateFromRequest(request, task);
        return taskMapper.toNode(task);
    }

    @Transactional
    public TaskStatusUpdateResponse updateTaskStatus(UUID taskId, UpdateTaskStatusRequest request, User currentUser) {
        Task task = taskUtil.getTaskById(taskId);

        task.setStatus(request.getStatus());

        if (request.getLoggedHours() != null && request.getLoggedHours() > 0) {
            CreateTimeLogRequest timeLogRequest = new CreateTimeLogRequest(request.getLoggedHours(), request.getComment());
            timeLogService.createTimeLog(task.getId(), timeLogRequest, currentUser);
        }

        task = taskRepository.save(task);

        // TODO: Implement logic to find and return unlocked tasks and the updated graph
        return TaskStatusUpdateResponse.builder()
                .updatedTask(taskMapper.toNode(task))
                .unlockedTasks(List.of())
                .graph(new ProjectGraphResponse(task.getProject().getId()))
                .build();
    }

    @Transactional
    public TaskNode assignTask(UUID taskId, AssignTaskRequest request) {
        Task task = taskUtil.getTaskById(taskId);

        Set<User> assignees = new HashSet<>(userRepository.findAllById(request.getUserIds()));
        task.setAssignees(assignees);

        task = taskRepository.save(task);
        return taskMapper.toNode(task);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        // TODO: Add logic to check for dependent tasks
        taskRepository.deleteById(taskId);
    }
}
