package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsu_taskgraph.core_api.entity.WikiPage;

import java.util.List;
import java.util.UUID;

@Repository
public interface WikiPageRepository extends JpaRepository<WikiPage, UUID> {
    List<WikiPage> findByProjectId(UUID projectId);

    List<WikiPage> findByProjectIdAndTaskId(UUID projectId, UUID taskId);
}
