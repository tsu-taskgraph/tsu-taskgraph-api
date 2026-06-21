package ru.tsu_taskgraph.core_api.dto.error;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CycleErrorResponse extends ErrorResponse {
    private List<UUID> cycle;

    public CycleErrorResponse(String message, LocalDateTime timestamp, List<UUID> cycle) {
        super(message, timestamp);
        this.cycle = cycle;
    }
}