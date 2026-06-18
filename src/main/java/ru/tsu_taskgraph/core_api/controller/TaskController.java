package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.task.*;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD задач, статусы, трудозатраты")
@SecurityRequirement(name = "BearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать задачу вручную")
    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    public TaskNode createTask(@PathVariable UUID projectId, @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    @GetMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Список задач проекта")
    @PreAuthorize("@projectSecurity.isViewer(#projectId)")
    public List<TaskNode> listTasks(@PathVariable UUID projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @GetMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить задачу (с enrichment, time logs, wiki)")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'VIEWER')")
    public TaskNode getTask(@PathVariable UUID taskId) {
        return taskService.getTask(taskId);
    }

    @PatchMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить задачу (title, description, даты, оценка, прогресс)",
            description = "Используется для обновления данных задачи, включая позицию на холсте (positionX / positionY при drag & drop в ReactFlow).")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'MEMBER')")
    public TaskNode updateTask(@PathVariable UUID taskId, @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(taskId, request);
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить задачу")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'ADMIN')")
    public void deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/tasks/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сменить статус (с авторазблокировкой + опциональный time log)")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'MEMBER')")
    public TaskStatusUpdateResponse updateTaskStatus(@PathVariable UUID taskId,
                                                     @RequestBody UpdateTaskStatusRequest request,
                                                     @AuthenticationPrincipal User currentUser) {
        return taskService.updateTaskStatus(taskId, request, currentUser);
    }

    @PutMapping("/tasks/{taskId}/assignees")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Назначить исполнителей (заменяет весь список)",
            description = "Передать пустой массив userIds = снять всех исполнителей. Аватары обновятся на узле ReactFlow.")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'ADMIN')")
    public TaskNode assignTask(@PathVariable UUID taskId, @RequestBody AssignTaskRequest request) {
        return taskService.assignTask(taskId, request);
    }
}
