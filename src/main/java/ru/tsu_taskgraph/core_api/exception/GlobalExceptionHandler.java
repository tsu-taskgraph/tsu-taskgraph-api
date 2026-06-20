package ru.tsu_taskgraph.core_api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tsu_taskgraph.core_api.dto.error.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка 400 Bad Request
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleCustomBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка неверного формата данных (400 Bad Request)
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException.class)
    public ErrorResponse handleInvalidFormatException(InvalidFormatException ex) {
        log.warn("Invalid format: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка 401 Unauthorized
    @Hidden
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка 403 Forbidden
    @Hidden
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка 404 Not Found
    @Hidden
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка 409 Conflict
    @Hidden
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ResourceConflictException.class, CycleDetectedException.class, OptimisticLockException.class})
    public ErrorResponse handleConflict(RuntimeException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        if (ex instanceof OptimisticLockException) {
            return new ErrorResponse("Ресурс был изменен другим пользователем. Пожалуйста, обновите данные и попробуйте снова.", LocalDateTime.now());
        }
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }

    // Обработка ошибок валидации @Valid (400 Bad Request)
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errors);
        return new ErrorResponse("Validation failed: [" + errors + "]", LocalDateTime.now());
    }

    // Обработка ошибок сохранения файлов (500 Internal Server Error)
    @Hidden
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FileStorageException.class)
    public ErrorResponse handleFileStorageException(FileStorageException ex) {
        log.error("A file storage error occurred", ex);
        return new ErrorResponse("Внутренняя ошибка сервера: не удалось сохранить файл", LocalDateTime.now());
    }

    // Обработка ошибок шифрования (500 Internal Server Error)
    @Hidden
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CryptoException.class)
    public ErrorResponse handleCryptoException(CryptoException ex) {
        log.error("An encryption/decryption error occurred", ex);
        return new ErrorResponse("Внутренняя ошибка сервера: не удалось выполнить криптографическую операцию", LocalDateTime.now());
    }

    // Fallback для непредвиденных серверных ошибок (500 Internal Server Error)
    @Hidden
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Unhandled server exception occurred", ex);
        return new ErrorResponse(ex.getMessage(), LocalDateTime.now());
    }
}