package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiMediaDto;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.WikiMediaService;
import ru.tsu_taskgraph.core_api.service.storage.StorageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Wiki", description = "Wiki-страницы проекта")
@SecurityRequirement(name = "BearerAuth")
public class WikiMediaController {

    private final WikiMediaService wikiMediaService;

    @GetMapping("/projects/{projectId}/wiki/media")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Список загруженных медиафайлов проекта")
    @PreAuthorize("@projectSecurity.isViewer(#projectId)")
    public List<WikiMediaDto> listWikiMedia(
            @PathVariable UUID projectId,
            @Parameter(description = "Фильтр по типу (image/*, application/pdf и т.д.)")
            @RequestParam(required = false) String mimeType
    ) {
        return wikiMediaService.getWikiMediaByProject(projectId, mimeType);
    }

    @PostMapping("/projects/{projectId}/wiki/media")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузить медиафайл для вставки в Wiki")
    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    public WikiMediaDto uploadWikiMedia(
            @PathVariable UUID projectId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser
    ) {
        return wikiMediaService.saveMedia(projectId, file, currentUser);
    }

    @DeleteMapping("/wiki/media/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить медиафайл")
    @PreAuthorize("@projectSecurity.canAccessWikiMedia(#mediaId, 'ADMIN')")
    public void deleteWikiMedia(@PathVariable UUID mediaId) {
        wikiMediaService.deleteMedia(mediaId);
    }

    @GetMapping("/projects/{projectId}/wiki/media/{filename:.+}")
    @Operation(summary = "Получить медиафайл")
    @PreAuthorize("@projectSecurity.isViewer(#projectId)")
    public ResponseEntity<Resource> getMedia(@PathVariable UUID projectId, @PathVariable String filename) {
        StorageService.StoredFile storedFile = wikiMediaService.getMedia(projectId, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .body(storedFile.resource());
    }
}
