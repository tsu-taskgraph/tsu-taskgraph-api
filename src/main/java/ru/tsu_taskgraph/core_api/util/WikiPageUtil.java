package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.WikiPage;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.WikiPageRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WikiPageUtil {

    private final WikiPageRepository wikiPageRepository;

    public WikiPage getWikiPageById(UUID pageId) {
        return wikiPageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Wiki-страница с id=" + pageId + " не найдена"));
    }
}
