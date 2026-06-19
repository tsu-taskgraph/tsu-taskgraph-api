package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.WikiMedia;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.WikiMediaRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WikiMediaUtil {

    private final WikiMediaRepository wikiMediaRepository;

    public WikiMedia getWikiMediaById(UUID mediaId) {
        return wikiMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media-файл с id=" + mediaId + " не найден"));
    }
}
