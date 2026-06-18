package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.task.CreateTimeLogRequest;
import ru.tsu_taskgraph.core_api.dto.task.TimeLogDto;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.TimeLogService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD задач, статусы, трудозатраты")
@SecurityRequirement(name = "BearerAuth")
public class TimeLogController {

    private final TimeLogService timeLogService;

    @PostMapping("/tasks/{taskId}/time-logs")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Залогировать время (MEMBER и выше)", operationId = "createTimeLog")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'MEMBER')")
    public TimeLogDto createTimeLog(@PathVariable UUID taskId,
                                    @RequestBody CreateTimeLogRequest request,
                                    @AuthenticationPrincipal User currentUser) {
        return timeLogService.createTimeLog(taskId, request, currentUser);
    }

    @GetMapping("/tasks/{taskId}/time-logs")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Журнал трудозатрат по задаче", operationId = "listTimeLogs")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'VIEWER')")
    public List<TimeLogDto> listTimeLogs(@PathVariable UUID taskId,
                                         @AuthenticationPrincipal User currentUser) {
        return timeLogService.getTimeLogsByTask(taskId);
    }

    @DeleteMapping("/time-logs/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись о времени (автор или ADMIN/OWNER)", operationId = "deleteTimeLog")
    @PreAuthorize("@projectSecurity.canDeleteTimeLog(#logId)")
    public void deleteTimeLog(@PathVariable UUID logId,
                              @AuthenticationPrincipal User currentUser) {
        timeLogService.deleteTimeLog(logId);
    }
}
