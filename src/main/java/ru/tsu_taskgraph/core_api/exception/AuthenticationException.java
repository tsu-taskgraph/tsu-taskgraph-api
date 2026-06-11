package ru.tsu_taskgraph.core_api.exception;

// Для ошибок 401 (Unauthorized)
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
