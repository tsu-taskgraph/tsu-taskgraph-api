package ru.tsu_taskgraph.core_api.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.tsu_taskgraph.core_api.entity.ActionLogEntry;
import ru.tsu_taskgraph.core_api.entity.ActionLogEventType;
import ru.tsu_taskgraph.core_api.entity.AuthorType;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActionLogSpecification {

    public static Specification<ActionLogEntry> projectIdEquals(UUID projectId) {
        return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<ActionLogEntry> actorTypeEquals(AuthorType actorType) {
        return (root, query, cb) -> cb.equal(root.get("actorType"), actorType);
    }

    public static Specification<ActionLogEntry> eventTypeEquals(ActionLogEventType eventType) {
        return (root, query, cb) -> cb.equal(root.get("eventType"), eventType);
    }

    public static Specification<ActionLogEntry> taskIdEquals(UUID taskId) {
        return (root, query, cb) -> cb.equal(root.get("metadata").get("taskId"), taskId.toString());
    }

    public static Specification<ActionLogEntry> createdAtAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<ActionLogEntry> createdAtBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
