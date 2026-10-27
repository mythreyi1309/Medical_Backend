package com.example.springboot;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Without this, any exception thrown in a controller is forwarded to /error,
 * which Spring Security blocks for anonymous callers -> you see a misleading 403.
 * Now the real reason is returned.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> denied(AccessDeniedException e) {
        return build(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> badRequest(Exception e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> runtime(RuntimeException e) {
        e.printStackTrace(); // visible in Aiven logs
        String msg = e.getMessage() == null ? "Server error" : e.getMessage();
        HttpStatus status = msg.toLowerCase().contains("invalid email or password")
                ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
        return build(status, msg);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus s, String m) {
        return ResponseEntity.status(s).body(Map.of("success", false, "message", String.valueOf(m)));
    }
}

