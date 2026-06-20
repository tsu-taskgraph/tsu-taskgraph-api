package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.entity.TimeLog;
import ru.tsu_taskgraph.core_api.entity.User;

@Getter
public class TimeLoggedEvent extends AuditEvent {
    private final TimeLog timeLog;
    private final User actor;

    public TimeLoggedEvent(Object source, TimeLog timeLog, User actor) {
        super(source);
        this.timeLog = timeLog;
        this.actor = actor;
    }
}