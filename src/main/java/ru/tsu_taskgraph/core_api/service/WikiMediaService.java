package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.controller.WikiMediaController;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiMediaDto;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.entity.WikiMedia;
import ru.tsu_taskgraph.core_api.mapper.WikiMediaMapper;
import ru.tsu_taskgraph.core_api.repository.WikiMediaRepository;
import ru.tsu_taskgraph.core_api.service.storage.StorageCategory;
import ru.tsu_taskgraph.core_api.service.storage.StorageService;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.WikiMediaUtil;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodName;

@Service
@RequiredArgsConstructor
public class WikiMediaService {

    private final WikiMediaRepository wikiMediaRepository;
    private final WikiMediaMapper wikiMediaMapper;
    private final StorageService storageService;
    private final ProjectUtil projectUtil;
    private final WikiMediaUtil wikiMediaUtil;

    @Transactional(readOnly = true)
    public List<WikiMediaDto> getWikiMediaByProject(UUID projectId, String mimeType) {
        List<WikiMedia> mediaList;
        if (mimeType != null && !mimeType.isBlank()) {
            mediaList = wikiMediaRepository.findByProjectIdAndMimeTypeStartingWith(projectId, mimeType);
        } else {
            mediaList = wikiMediaRepository.findByProjectId(projectId);
        }
        return wikiMediaMapper.toDtoList(mediaList);
    }

    @Transactional
    public WikiMediaDto saveMedia(UUID projectId, MultipartFile file, User currentUser) {
        Project project = projectUtil.getProjectById(projectId);

        String filename = storageService.store(file, StorageCategory.MEDIA, project.getId().toString());

        String url = fromMethodName(WikiMediaController.class, "getMedia", project.getId(), filename).build().toUriString();

        WikiMedia media = WikiMedia.builder()
                .project(project)
                .filename(filename)
                .originalFilename(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .url(url)
                .uploadedBy(currentUser)
                .build();

        media = wikiMediaRepository.save(media);
        return wikiMediaMapper.toDto(media);
    }

    @Transactional
    public void deleteMedia(UUID mediaId) {
        WikiMedia media = wikiMediaUtil.getWikiMediaById(mediaId);
        storageService.delete(StorageCategory.MEDIA, media.getProject().getId().toString(), media.getFilename());
        wikiMediaRepository.delete(media);
    }

    public StorageService.StoredFile getMedia(UUID projectId, String filename) {
        return storageService.load(StorageCategory.MEDIA, projectId.toString(), filename);
    }
}
