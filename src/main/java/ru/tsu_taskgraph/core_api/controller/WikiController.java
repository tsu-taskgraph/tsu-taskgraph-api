package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.wiki.CreateWikiPageRequest;
import ru.tsu_taskgraph.core_api.dto.wiki.UpdateWikiPageRequest;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageDto;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageSummaryDto;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.WikiService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Wiki", description = "Wiki-страницы проекта")
@SecurityRequirement(name = "BearerAuth")
public class WikiController {

    private final WikiService wikiService;

    @GetMapping("/projects/{projectId}/wiki")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Список Wiki-страниц проекта")
    @PreAuthorize("@projectSecurity.isViewer(#projectId)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список страниц получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')")
    })
    public List<WikiPageSummaryDto> listWikiPages(
            @PathVariable UUID projectId,
            @Parameter(description = "Фильтр — только страницы конкретной задачи")
            @RequestParam(required = false) UUID taskId
    ) {
        return wikiService.getWikiPagesByProject(projectId, taskId);
    }

    @PostMapping("/projects/{projectId}/wiki")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать Wiki-страницу вручную")
    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Страница успешно создана"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Проект с ID 123 не найден'\n" +
                    "* 'Задача с id=123 не найдена'")
    })
    public WikiPageDto createWikiPage(@PathVariable UUID projectId,
                                      @Valid @RequestBody CreateWikiPageRequest request,
                                      @AuthenticationPrincipal User currentUser) {
        return wikiService.createWikiPage(projectId, request, currentUser);
    }

    @GetMapping("/wiki/{pageId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить Wiki-страницу (полный контент)")
    @PreAuthorize("@projectSecurity.canAccessWikiPage(#pageId, 'VIEWER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Wiki-страница с id=123 не найдена'")
    })
    public WikiPageDto getWikiPage(@PathVariable UUID pageId) {
        return wikiService.getWikiPage(pageId);
    }

    @PutMapping("/wiki/{pageId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить Wiki-страницу")
    @PreAuthorize("@projectSecurity.canAccessWikiPage(#pageId, 'MEMBER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница успешно обновлена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Wiki-страница с id=123 не найдена'"),
            @ApiResponse(responseCode = "409", description = "Конфликт версий. Возможное сообщение:\n" +
                    "* 'Страница была изменена другим пользователем. Пожалуйста, обновите страницу.'")
    })
    public WikiPageDto updateWikiPage(@PathVariable UUID pageId,
                                      @Valid @RequestBody UpdateWikiPageRequest request,
                                      @AuthenticationPrincipal User currentUser) {
        return wikiService.updateWikiPage(pageId, request, currentUser);
    }

    @DeleteMapping("/wiki/{pageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить Wiki-страницу")
    @PreAuthorize("@projectSecurity.canAccessWikiPage(#pageId, 'ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Страница успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Wiki-страница с id=123 не найдена'")
    })
    public void deleteWikiPage(@PathVariable UUID pageId) {
        wikiService.deleteWikiPage(pageId);
    }
}