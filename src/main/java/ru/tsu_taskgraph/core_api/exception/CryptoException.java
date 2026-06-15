package ru.tsu_taskgraph.core_api.exception;

public class CryptoException extends RuntimeException {
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
