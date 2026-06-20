package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.log.ActionLogEntryDto;
import ru.tsu_taskgraph.core_api.entity.ActionLogEventType;
import ru.tsu_taskgraph.core_api.entity.AuthorType;
import ru.tsu_taskgraph.core_api.service.ActionLogService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/action-log")
@RequiredArgsConstructor
@Tag(name = "ActionLog", description = "Лог действий пользователей и ИИ")
@SecurityRequirement(name = "BearerAuth")
public class ActionLogController {

    private final ActionLogService actionLogService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Лог действий проекта")
    @PreAuthorize("@projectSecurity.isViewer(#projectId)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лог действий получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')")
    })
    public Page<ActionLogEntryDto> getActionLog(
            @PathVariable UUID projectId,
            @Parameter(description = "Фильтр по типу актора") @RequestParam(required = false) AuthorType actorType,
            @Parameter(description = "Фильтр по типу события") @RequestParam(required = false) ActionLogEventType eventType,
            @Parameter(description = "Фильтр по ID задачи") @RequestParam(required = false) UUID taskId,
            @Parameter(description = "Начало временного диапазона (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конец временного диапазона (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 50, sort = "createdAt,desc") Pageable pageable
    ) {
        return actionLogService.getActionLog(projectId, actorType, eventType, taskId, from, to, pageable);
    }
}
