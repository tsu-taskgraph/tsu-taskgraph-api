package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.tsu_taskgraph.core_api.entity.Task;

@Getter
public class SystemGraphRefreshEvent extends ApplicationEvent {
    private final Task triggerTask;

    public SystemGraphRefreshEvent(Object source, Task triggerTask) {
        super(source);
        this.triggerTask = triggerTask;
    }
}
