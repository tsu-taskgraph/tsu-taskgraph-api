package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.ai.EnrichTaskCallback;
import ru.tsu_taskgraph.core_api.dto.wiki.CreateWikiPageRequest;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskEnrichment;
import ru.tsu_taskgraph.core_api.exception.AuthenticationException;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrichmentCallbackService {

    private final TaskUtil taskUtil;
    private final TaskRepository taskRepository;
    private final WikiService wikiService;
    private final AuditEventPublisher auditEventPublisher;

    @Value("${ai-bridge.internal-secret}")
    private String internalSecret;

    @Transactional
    public void processCallback(EnrichTaskCallback callback, String secret) {
        if (!internalSecret.equals(secret)) {
            throw new AuthenticationException("Невалидный X-Internal-Secret");
        }

        log.info("Получен колбэк для задачи {}: статус {}", callback.getTaskId(), callback.getStatus());

        Task task = taskUtil.getTaskById(callback.getTaskId());

        if ("SUCCESS".equals(callback.getStatus())) {
            TaskEnrichment enrichment = TaskEnrichment.builder()
                    .checklist(callback.getChecklist())
                    .pitfalls(callback.getPitfalls())
                    .links(callback.getLinks())
                    .rawMarkdown(callback.getRawMarkdown())
                    .build();
            task.setEnrichment(enrichment);

            if (StringUtils.hasText(callback.getWikiDraft())) {
                parseAndCreateWikiPage(task, callback.getWikiDraft());
            }

            taskRepository.save(task);
            auditEventPublisher.publishAiEnrichmentCompletedEvent(this, task);
            log.info("Задача {} успешно обогащена.", task.getId());
        } else {
            log.error("Ошибка обогащения задачи {}: {}", task.getId(), callback.getError());
            auditEventPublisher.publishAiEnrichmentFailedEvent(this, task, callback.getError());
        }
    }

    private void parseAndCreateWikiPage(Task task, String wikiDraft) {
        try (BufferedReader reader = new BufferedReader(new StringReader(wikiDraft))) {
            String title = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# ")) {
                    title = line.substring(2).trim();
                    break;
                }
            }

            String content = reader.lines().collect(Collectors.joining("\n"));

            if (title == null) {
                title = "Wiki: " + task.getTitle();
                content = wikiDraft; // Если заголовок не найден, используем весь текст как контент
            }

            CreateWikiPageRequest wikiRequest = CreateWikiPageRequest.builder()
                    .title(title)
                    .content(content)
                    .taskId(task.getId())
                    .build();
            wikiService.createWikiPage(task.getProject().getId(), wikiRequest, null);

        } catch (Exception e) {
            log.error("Не удалось распарсить и создать Wiki-страницу для задачи {}", task.getId(), e);
        }
    }
}