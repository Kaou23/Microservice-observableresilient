package com.example.book.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiErrors {

    private static final Logger log = LoggerFactory.getLogger(ApiErrors.class);

    /**
     * Handle IllegalStateException (e.g., no stock available).
     * Returns 409 Conflict status.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        log.warn("Conflict error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse("Conflict", ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    /**
     * Handle RuntimeException for general errors.
     * Returns 400 Bad Request for "not found" messages, 500 otherwise.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage());

        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Server Error", ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Handle all other exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Server Error", ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Create a standardized error response.
     */
    private Map<String, Object> createErrorResponse(String error, String message, int status) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "error", error,
                "message", message != null ? message : "An unexpected error occurred");
    }
}
