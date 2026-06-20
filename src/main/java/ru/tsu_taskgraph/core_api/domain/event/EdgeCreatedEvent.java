package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class EdgeCreatedEvent extends AuditEvent {
    private final Edge edge;
    private final User actor;

    public EdgeCreatedEvent(Object source, Edge edge, User actor) {
        super(source);
        this.edge = edge;
        this.actor = actor;
    }
}