package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.dto.project.CreateEdgeRequest;
import ru.tsu_taskgraph.core_api.dto.project.EdgeResponse;
import ru.tsu_taskgraph.core_api.service.EdgeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Edges", description = "Рёбра (зависимости) DAG")
@SecurityRequirement(name = "BearerAuth")
public class EdgeController {

    private final EdgeService edgeService;

    @PostMapping("/projects/{projectId}/edges")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить ребро (зависимость)")
    @PreAuthorize("@projectSecurity.isAdmin(#projectId)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ребро успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос. Возможные сообщения:\n" +
                    "* 'Обе задачи должны принадлежать указанному проекту.'\n" +
                    "* 'Нельзя установить зависимость задачи на саму себя.'\n" +
                    "* 'Нельзя добавить подзадачу к завершенной или пропущенной задаче.'\n" +
                    "* 'Такая зависимость уже существует.'\n" +
                    "* 'Добавление этой зависимости создаёт цикл в графе задач.'"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Задача с id=123 не найдена'")
    })
    public EdgeResponse createEdge(@PathVariable UUID projectId,
                                   @Valid @RequestBody CreateEdgeRequest request) {
        return edgeService.createEdge(projectId, request);
    }

    @DeleteMapping("/edges/{edgeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить ребро (зависимость)")
    @PreAuthorize("@projectSecurity.canAccessEdge(#edgeId, 'ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ребро успешно удалено"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (сообщение: 'Доступ запрещен')"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Ребро с id=123 не найдено'")
    })
    public void deleteEdge(@PathVariable UUID edgeId) {
        edgeService.deleteEdge(edgeId);
    }
}