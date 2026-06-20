package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class AuditEvent extends ApplicationEvent {

    public AuditEvent(Object source) {
        super(source);
    }
}
