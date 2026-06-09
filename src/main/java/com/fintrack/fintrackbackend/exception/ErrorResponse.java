package com.fintrack.fintrackbackend.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int code, String message) {
        this(code, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(int code, String message, Map<String, String> fieldErrors) {
        this(code, message, LocalDateTime.now(), fieldErrors);
    }
}
