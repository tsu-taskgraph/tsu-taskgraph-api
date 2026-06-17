package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.tsu_taskgraph.core_api.config.OpenApiConfig;
import ru.tsu_taskgraph.core_api.dto.project.*;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.ProjectService;
import ru.tsu_taskgraph.core_api.util.UserUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Управление проектами")
public class ProjectController {

    private final ProjectService projectService;
    private final UserUtil userUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать проект + AI-декомпозиция (Фаза 1)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public ProjectDto createProject(@Valid @RequestBody CreateProjectRequest request) {
        User currentUser = userUtil.getCurrentUserFromContext();
        return projectService.createProject(request, currentUser.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Список проектов пользователя",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public Page<ProjectDto> getUserProjects(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        User currentUser = userUtil.getCurrentUserFromContext();
        return projectService.getUserProjects(currentUser.getId(), pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.hasAccess(#id)")
    @Operation(
            summary = "Получить проект",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public ProjectDto getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isOwnerOrAdmin(#id)")
    @Operation(
            summary = "Обновить метаданные (OWNER/ADMIN)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public ProjectDto updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectSecurity.isOwner(#id)")
    @Operation(
            summary = "Удалить проект (только OWNER)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/{id}/members")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.hasAccess(#id)")
    @Operation(
            summary = "Список участников проекта",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public List<ProjectMemberDto> listProjectMembers(@PathVariable UUID id) {
        return projectService.listProjectMembers(id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@projectSecurity.isOwnerOrAdmin(#id)")
    @Operation(
            summary = "Пригласить участника (OWNER/ADMIN)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public ProjectMemberDto inviteMember(
            @PathVariable UUID id,
            @Valid @RequestBody InviteMemberRequest request
    ) {
        return projectService.inviteMember(id, request);
    }

    @PatchMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.isOwner(#id)")
    @Operation(
            summary = "Изменить роль участника (только OWNER)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
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
    @Operation(
            summary = "Удалить участника (OWNER или сам пользователь)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public void removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId
    ) {
        projectService.removeMember(id, userId);
    }

    @GetMapping("/{id}/graph")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@projectSecurity.hasAccess(#id)")
    @Operation(
            summary = "Полный граф (узлы + рёбра + назначения)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public void getProjectGraph(@PathVariable UUID id) {
        // TODO: Реализовать получение графа задач проекта
    }
}
