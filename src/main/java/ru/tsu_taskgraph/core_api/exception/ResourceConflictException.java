package ru.tsu_taskgraph.core_api.exception;

// Для ошибок 409 (Conflict)
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}