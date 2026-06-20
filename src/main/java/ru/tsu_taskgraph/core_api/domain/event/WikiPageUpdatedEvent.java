package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.entity.WikiPage;

@Getter
public class WikiPageUpdatedEvent extends AuditEvent {
    private final WikiPage wikiPage;
    private final User actor;

    public WikiPageUpdatedEvent(Object source, WikiPage wikiPage, User actor) {
        super(source);
        this.wikiPage = wikiPage;
        this.actor = actor;
    }
}