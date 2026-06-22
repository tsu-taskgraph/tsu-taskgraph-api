package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.Task;

@Getter
public class AiEnrichmentFailedEvent extends AuditEvent {
    private final Task task;
    private final String error;

    public AiEnrichmentFailedEvent(Object source, Task task, String error) {
        super(source);
        this.task = task;
        this.error = error;
    }
}