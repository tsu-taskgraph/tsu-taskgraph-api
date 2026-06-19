package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsu_taskgraph.core_api.entity.WikiMedia;

import java.util.List;
import java.util.UUID;

@Repository
public interface WikiMediaRepository extends JpaRepository<WikiMedia, UUID> {
    List<WikiMedia> findByProjectId(UUID projectId);

    List<WikiMedia> findByProjectIdAndMimeTypeStartingWith(UUID projectId, String mimeType);
}
