package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    List<Task> findByProject(Project project);

    List<Task> findByProjectId(UUID projectId);

    @Query("SELECT e.sourceTask FROM Edge e WHERE e.targetTask.id = :targetTaskId")
    List<Task> findByTargetTaskId(@Param("targetTaskId") UUID targetTaskId);
}
