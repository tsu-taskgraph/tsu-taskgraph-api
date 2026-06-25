package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.wiki.CreateWikiPageRequest;
import ru.tsu_taskgraph.core_api.dto.wiki.UpdateWikiPageRequest;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageDto;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageSummaryDto;
import ru.tsu_taskgraph.core_api.entity.*;
import ru.tsu_taskgraph.core_api.exception.ResourceConflictException;
import ru.tsu_taskgraph.core_api.mapper.WikiMapper;
import ru.tsu_taskgraph.core_api.repository.WikiPageRepository;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.TaskUtil;
import ru.tsu_taskgraph.core_api.util.UserUtil;
import ru.tsu_taskgraph.core_api.util.WikiPageUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WikiService {

    private final WikiPageRepository wikiPageRepository;
    private final WikiMapper wikiMapper;
    private final ProjectUtil projectUtil;
    private final TaskUtil taskUtil;
    private final WikiPageUtil wikiPageUtil;
    private final AuditEventPublisher auditEventPublisher;
    private final UserUtil userUtil;

    @Transactional(readOnly = true)
    public List<WikiPageSummaryDto> getWikiPagesByProject(UUID projectId, UUID taskId) {
        List<WikiPage> pages;
        if (taskId != null) {
            pages = wikiPageRepository.findByProjectIdAndTaskId(projectId, taskId);
        } else {
            pages = wikiPageRepository.findByProjectId(projectId);
        }
        return wikiMapper.toSummaryDtoList(pages);
    }

    @Transactional
    public WikiPageDto createWikiPage(UUID projectId, CreateWikiPageRequest request, User currentUser) {
        Project project = projectUtil.getProjectById(projectId);
        Task task = null;
        if (request.getTaskId() != null) {
            task = taskUtil.getTaskById(request.getTaskId());
        }

        WikiPage.WikiPageBuilder pageBuilder = WikiPage.builder()
                .project(project)
                .task(task)
                .title(request.getTitle())
                .content(request.getContent())
                .version(1);

        if (currentUser != null) {
            pageBuilder.authorId(currentUser.getId()).authorType(AuthorType.USER);
        } else {
            pageBuilder.authorType(AuthorType.AI);
        }

        WikiPage page = pageBuilder.build();
        page = wikiPageRepository.save(page);

        auditEventPublisher.publishWikiPageCreatedEvent(this, page, currentUser);

        return wikiMapper.toDto(page);
    }

    @Transactional(readOnly = true)
    public WikiPageDto getWikiPage(UUID pageId) {
        WikiPage page = wikiPageUtil.getWikiPageById(pageId);
        return wikiMapper.toDto(page);
    }

    @Transactional
    public WikiPageDto updateWikiPage(UUID pageId, UpdateWikiPageRequest request, User currentUser) {
        WikiPage page = wikiPageUtil.getWikiPageById(pageId);

//        if (!Objects.equals(request.getVersion(), page.getVersion())) {
//            throw new ResourceConflictException("Страница была изменена другим пользователем. Пожалуйста, обновите страницу.");
//        }

        wikiMapper.updateFromRequest(request, page);

        page.setAuthorId(currentUser.getId());
        page.setAuthorType(AuthorType.USER);

        auditEventPublisher.publishWikiPageUpdatedEvent(this, page, currentUser);

        return wikiMapper.toDto(page);
    }

    @Transactional
    public void deleteWikiPage(UUID pageId) {
        wikiPageRepository.deleteById(pageId);
    }
}