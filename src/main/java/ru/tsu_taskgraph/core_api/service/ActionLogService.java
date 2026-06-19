package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.log.ActionLogEntryDto;
import ru.tsu_taskgraph.core_api.entity.ActionLogEntry;
import ru.tsu_taskgraph.core_api.entity.ActionLogEventType;
import ru.tsu_taskgraph.core_api.entity.AuthorType;
import ru.tsu_taskgraph.core_api.mapper.ActionLogMapper;
import ru.tsu_taskgraph.core_api.repository.ActionLogRepository;
import ru.tsu_taskgraph.core_api.repository.specification.ActionLogSpecification;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final ActionLogMapper actionLogMapper;

    @Transactional(readOnly = true)
    public Page<ActionLogEntryDto> getActionLog(UUID projectId, AuthorType actorType, ActionLogEventType eventType, UUID taskId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<ActionLogEntry> spec = Specification.where(ActionLogSpecification.projectIdEquals(projectId))
                .and(actorType != null ? ActionLogSpecification.actorTypeEquals(actorType) : null)
                .and(eventType != null ? ActionLogSpecification.eventTypeEquals(eventType) : null)
                .and(taskId != null ? ActionLogSpecification.taskIdEquals(taskId) : null)
                .and(from != null ? ActionLogSpecification.createdAtAfter(from) : null)
                .and(to != null ? ActionLogSpecification.createdAtBefore(to) : null);

        return actionLogRepository.findAll(spec, pageable).map(actionLogMapper::toDto);
    }
}
