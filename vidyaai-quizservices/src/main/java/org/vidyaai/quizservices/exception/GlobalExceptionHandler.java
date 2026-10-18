package org.vidyaai.quizservices.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.vidyaai.quizservices.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuizNotFound(
            QuizNotFoundException ex, WebRequest request) {
        log.error("Quiz not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(AttemptNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAttemptNotFound(
            AttemptNotFoundException ex, WebRequest request) {
        log.error("Attempt not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(MaxAttemptsReachedException.class)
    public ResponseEntity<ErrorResponse> handleMaxAttempts(
            MaxAttemptsReachedException ex, WebRequest request) {
        log.warn("Max attempts reached: {}", ex.getMessage());
        return build(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(AttemptCooldownException.class)
    public ResponseEntity<ErrorResponse> handleCooldown(
            AttemptCooldownException ex, WebRequest request) {
        log.warn("Attempt cooldown active: {}", ex.getMessage());
        return build(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(QuizExpiredException.class)
    public ResponseEntity<ErrorResponse> handleQuizExpired(
            QuizExpiredException ex, WebRequest request) {
        log.warn("Quiz expired: {}", ex.getMessage());
        return build(HttpStatus.GONE, "GONE", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "QUIZ_007",
                "Access denied", request, null);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(
            InvalidRequestException ex, WebRequest request) {
        log.error("Invalid request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getErrorCode(),
                ex.getMessage(), request, ex.getErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        log.error("Validation failed: {}", errors);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "QUIZ_003",
                "Validation failed", request, errors);
    }

    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ErrorResponse> handleCacheOperation(
            CacheOperationException ex, WebRequest request) {
        log.error("Cache error: {}", ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_ERROR", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(QuizServiceException.class)
    public ResponseEntity<ErrorResponse> handleQuizService(
            QuizServiceException ex, WebRequest request) {
        log.error("Quiz service error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "QUIZ_SERVICE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "QUIZ_003",
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "QUIZ_999", "An unexpected error occurred.", request, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error,
                                                String errorCode, String message,
                                                WebRequest request,
                                                Map<String, String> validationErrors) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .errorCode(errorCode)
                .message(message)
                .validationErrors(validationErrors)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(body, status);
    }
}