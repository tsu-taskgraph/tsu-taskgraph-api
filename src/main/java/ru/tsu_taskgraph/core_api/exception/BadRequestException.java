package ru.tsu_taskgraph.core_api.exception;

// Для ошибок 400 (Bad Request)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}