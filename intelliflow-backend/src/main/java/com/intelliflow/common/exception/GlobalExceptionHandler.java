package com.intelliflow.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Interceptor implementing RFC 7807 Problem Details for HTTP APIs.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom business exceptions.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetail> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.warn("Business Exception [{}] triggered at path {}: {}", ex.getErrorCode().getCode(), request.getRequestURI(), ex.getMessage());
        
        ErrorCode errorCode = ex.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
        problemDetail.setTitle(errorCode.name());
        problemDetail.setType(URI.create("https://api.intelliflow.com/errors/" + errorCode.getCode().toLowerCase()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", errorCode.getCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(problemDetail);
    }

    /**
     * Handles Jakarta Bean Validation errors (@Valid DTO failure).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation Failure at path {}: {} errors", request.getRequestURI(), ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for request parameters");
        problemDetail.setTitle("INVALID_REQUEST_PARAMETERS");
        problemDetail.setType(URI.create("https://api.intelliflow.com/errors/validation-error"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ErrorCode.BAD_REQUEST.getCode());
        problemDetail.setProperty("validationErrors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Fallback handler for unhandled runtime exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandledException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled Internal Server Error at path {}", request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.");
        problemDetail.setTitle("INTERNAL_SERVER_ERROR");
        problemDetail.setType(URI.create("https://api.intelliflow.com/errors/internal-error"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ErrorCode.INTERNAL_ERROR.getCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
