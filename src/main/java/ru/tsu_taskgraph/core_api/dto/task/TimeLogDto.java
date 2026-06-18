package ru.tsu_taskgraph.core_api.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLogDto {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private String userDisplayName;
    private Double hours;
    private String comment;
    private LocalDateTime loggedAt;
}
