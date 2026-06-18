package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, UUID> {
    List<Edge> findByProject(Project project);
    List<Edge> findByProjectId(UUID projectId);
    boolean existsBySourceTaskAndTargetTask(Task source, Task target);
    boolean existsByTargetTask(Task target);
}
