package ru.tsu_taskgraph.core_api.domain.event;

import lombok.Getter;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonResponse;
import ru.tsu_taskgraph.core_api.entity.Project;

@Getter
public class AiSkeletonGeneratedEvent extends AuditEvent {
    private final Project project;
    private final GenerateSkeletonResponse response;

    public AiSkeletonGeneratedEvent(Object source, Project project, GenerateSkeletonResponse response) {
        super(source);
        this.project = project;
        this.response = response;
    }
}