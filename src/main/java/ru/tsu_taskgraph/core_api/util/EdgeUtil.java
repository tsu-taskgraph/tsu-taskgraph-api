package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EdgeUtil {

    private final EdgeRepository edgeRepository;

    public Edge getEdgeById(UUID edgeId) {
        return edgeRepository.findById(edgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Edge not found: " + edgeId));
    }
}
