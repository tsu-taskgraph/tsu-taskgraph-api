package ru.tsu_taskgraph.core_api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AiCycleException extends RuntimeException {
    private final List<String> cycle;
    private final boolean smartRecoveryAvailable;

    public AiCycleException(String message, List<String> cycle, boolean smartRecoveryAvailable) {
        super(message);
        this.cycle = cycle;
        this.smartRecoveryAvailable = smartRecoveryAvailable;
    }
}