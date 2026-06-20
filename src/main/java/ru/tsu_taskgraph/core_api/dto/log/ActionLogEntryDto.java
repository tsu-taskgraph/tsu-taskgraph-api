package ru.tsu_taskgraph.core_api.dto.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.ActionLogEventType;
import ru.tsu_taskgraph.core_api.entity.AuthorType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionLogEntryDto {
    private UUID id;
    private UUID projectId;
    private AuthorType actorType;
    private ActionLogEventType eventType;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}