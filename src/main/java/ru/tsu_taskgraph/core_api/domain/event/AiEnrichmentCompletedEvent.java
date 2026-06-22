package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Task;

@Getter
public class AiEnrichmentCompletedEvent extends AuditEvent {
    private final Task task;

    public AiEnrichmentCompletedEvent(Object source, Task task) {
        super(source);
        this.task = task;
    }
}