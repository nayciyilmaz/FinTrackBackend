package com.fintrack.fintrackbackend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("BusinessException: code={}, message={}", ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        log.warn("ValidationException: errors={}", fieldErrors);
        ErrorResponse response = new ErrorResponse(400, "Validasyon hatası", fieldErrors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("UnexpectedException: message={}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }
}
