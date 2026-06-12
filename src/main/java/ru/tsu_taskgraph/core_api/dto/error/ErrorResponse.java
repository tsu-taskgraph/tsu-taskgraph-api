package ru.tsu_taskgraph.core_api.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp
) {
}