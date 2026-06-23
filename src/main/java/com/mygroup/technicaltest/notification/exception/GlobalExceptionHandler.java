package com.mygroup.technicaltest.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Translates domain and validation errors into consistent JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Missing or blank required fields → 400 with per-field messages. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return errorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    /** Unsupported channel value → 400. */
    @ExceptionHandler(InvalidChannelException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidChannel(InvalidChannelException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    /** Misconfigured channel with no provider → 500. */
    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProviderNotFound(ProviderNotFoundException ex) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), List.of());
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message,
                                                              List<String> details) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "details", details);
        return ResponseEntity.status(status).body(body);
    }
}