package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.ai.AiRequestConfig;
import ru.tsu_taskgraph.core_api.dto.project.*;
import ru.tsu_taskgraph.core_api.entity.ProjectStatus;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.ProjectGraphService;
import ru.tsu_taskgraph.core_api.service.ProjectService;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Управление проектами")
@SecurityRequirement(name = "BearerAuth")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectGraphService projectGraphService;
    private final UserUtil userUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать проект + AI-декомпозиция (Фаза 1)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос. Возможное сообщение:\n" +
                    "* 'AI-провайдер не сконфигурирован. Передайте настройки в X-AI-* заголовках или сохраните их в профиле.'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public ProjectDto createProject(@Valid @RequestBody CreateProjectRequest request,
                                    @RequestHeader(value = "X-AI-Provider", required = false) String provider,
                                    @RequestHeader(value = "X-AI-API-Key", required = false) String apiKey,
                                    @RequestHeader(value = "X-AI-Model", required = false) String model,
                                    @RequestHeader(value = "X-Custom-Base-URL", required = false) String customBaseUrl) {
        User currentUser = userUtil.getCurrentUserFromContext();
        AiRequestConfig aiConfig = AiRequestConfig.builder()
                .provider(provider)
                .apiKey(apiKey)
                .model(model)
                .customBaseUrl(customBaseUrl)
                .build();
        return projectService.createProject(request, currentUser.getId(), aiConfig);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Список проектов пользователя")
    public Page<ProjectDto> getUserProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        User currentUser = userUtil.getCurrentUserFromContext();
        return projectService.getUserProjects(currentUser.getId(), status, name, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isViewer(#id)")
    @Operation(summary = "Получить проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Проект с ID 123 не найден'")
    })
    public ProjectDto getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isAdmin(#id)")
    @Operation(summary = "Обновить метаданные (OWNER/ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Проект с ID 123 не найден'"),
            @ApiResponse(responseCode = "409", description = "Конфликт версий. Возможное сообщение:\n" +
                    "* 'Проект был изменен другим пользователем. Пожалуйста, обновите страницу.'")
    })
    public ProjectDto updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectSecurity.isOwner(#id)")
    @Operation(summary = "Удалить проект (только OWNER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Проект с ID 123 не найден'")
    })
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/{id}/members")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isViewer(#id)")
    @Operation(summary = "Список участников проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список участников получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Проект с ID 123 не найден'")
    })
    public List<ProjectMemberDto> listProjectMembers(@PathVariable UUID id) {
        return projectService.listProjectMembers(id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@projectSecurity.isAdmin(#id)")
    @Operation(summary = "Пригласить участника (OWNER/ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Участник успешно приглашен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Проект с ID 123 не найден'\n" +
                    "* 'Пользователь с email=123 не найден'"),
            @ApiResponse(responseCode = "409", description = "Конфликт. Возможное сообщение:\n" +
                    "* 'Пользователь уже является участником проекта'")
    })
    public ProjectMemberDto inviteMember(
            @PathVariable UUID id,
            @Valid @RequestBody InviteMemberRequest request
    ) {
        return projectService.inviteMember(id, request);
    }

    @PatchMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isOwner(#id)")
    @Operation(summary = "Изменить роль участника (только OWNER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль участника успешно изменена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Проект с ID 123 не найден'\n" +
                    "* 'Пользователь с id=123 не найден'\n" +
                    "* 'Участник с ID 123 не найден в проекте с ID 123'")
    })
    public ProjectMemberDto updateMemberRole(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateMemberRoleRequest request
    ) {
        return projectService.updateMemberRole(id, userId, request);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectSecurity.isOwner(#id) or principal.id == #userId")
    @Operation(summary = "Удалить участника (OWNER или сам пользователь)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Участник успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Проект с ID 123 не найден'\n" +
                    "* 'Пользователь с id=123 не найден'\n" +
                    "* 'Участник с ID 123 не найден в проекте с ID 123'")
    })
    public void removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId
    ) {
        projectService.removeMember(id, userId);
    }

    @GetMapping("/{id}/graph")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isViewer(#id)")
    @Operation(summary = "Полный граф (узлы + рёбра + назначения)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Граф проекта получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Проект с ID 123 не найден'")
    })
    public ProjectGraphResponse getProjectGraph(@PathVariable UUID id) {
        return projectGraphService.getProjectGraph(id);
    }
}