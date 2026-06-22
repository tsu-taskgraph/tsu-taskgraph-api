package ru.tsu_taskgraph.core_api.dto.error;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CycleErrorResponse extends ErrorResponse {
    private List<String> cycle;

    public CycleErrorResponse(String message, LocalDateTime timestamp, List<String> cycle) {
        super(message, timestamp);
        this.cycle = cycle;
    }
}