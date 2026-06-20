package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Время успешно залогировано"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос. Возможные причины:\n" +
                    "* Некорректный формат времени. Ожидается 'ЧЧ:ММ' или число.\n" +
                    "* Валидация (например, 'hours must be greater than 0')"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Задача с id=123 не найдена'")
    })
    public TimeLogDto createTimeLog(@PathVariable UUID taskId,
                                    @RequestBody CreateTimeLogRequest request,
                                    @AuthenticationPrincipal User currentUser) {
        return timeLogService.createTimeLog(taskId, request, currentUser);
    }

    @GetMapping("/tasks/{taskId}/time-logs")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Журнал трудозатрат по задаче", operationId = "listTimeLogs")
    @PreAuthorize("@projectSecurity.canAccessTask(#taskId, 'VIEWER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Журнал трудозатрат получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Задача с id=123 не найдена'")
    })
    public List<TimeLogDto> listTimeLogs(@PathVariable UUID taskId) {
        return timeLogService.getTimeLogsByTask(taskId);
    }

    @DeleteMapping("/tasks/{taskId}/time-logs/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись о времени (автор или ADMIN/OWNER)", operationId = "deleteTimeLog")
    @PreAuthorize("@projectSecurity.canDeleteTimeLog(#logId, #taskId)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Запись о времени успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос. Возможное сообщение:\n" +
                    "* 'Трудозатраты с id=123 не относятся к Задаче с id=123'"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Трудозатраты с id=123 не найдены'\n" +
                    "* 'Задача с id=123 не найдена'")
    })
    public void deleteTimeLog(@PathVariable UUID taskId, @PathVariable UUID logId) {
        timeLogService.deleteTimeLog(taskId, logId);
    }
}