package ru.tsu_taskgraph.core_api.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskCategory;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.util.UUID;

public class TaskSpecification {

    public static Specification<Task> projectIdEquals(UUID projectId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> statusEquals(TaskStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> categoryEquals(TaskCategory category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Task> assigneeIdEquals(UUID assigneeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("assignees").get("id"), assigneeId);
    }
}
