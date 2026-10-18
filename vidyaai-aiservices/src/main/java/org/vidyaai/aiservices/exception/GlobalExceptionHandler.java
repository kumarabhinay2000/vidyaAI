package org.vidyaai.aiservices.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.vidyaai.aiservices.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(LlmTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleLlmTimeout(
            LlmTimeoutException ex, WebRequest request) {
        log.error("LLM timeout: {} (timeout={}ms)", ex.getMessage(), ex.getTimeoutMs());
        return buildResponse(HttpStatus.GATEWAY_TIMEOUT, "GATEWAY_TIMEOUT", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(LlmRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleLlmRateLimit(
            LlmRateLimitException ex, WebRequest request) {
        log.warn("LLM rate limit exceeded: {} (retryAfter={}s)", ex.getMessage(), ex.getRetryAfterSeconds());
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(LlmProviderException.class)
    public ResponseEntity<ErrorResponse> handleLlmProvider(
            LlmProviderException ex, WebRequest request) {
        log.error("LLM provider error (provider={}): {}", ex.getProvider(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(TranscriptExtractionException.class)
    public ResponseEntity<ErrorResponse> handleTranscriptExtraction(
            TranscriptExtractionException ex, WebRequest request) {
        log.error("Transcript extraction error: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(VectorStoreException.class)
    public ResponseEntity<ErrorResponse> handleVectorStore(
            VectorStoreException ex, WebRequest request) {
        log.error("Vector store error: {}", ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_STORE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(PromptTemplateException.class)
    public ResponseEntity<ErrorResponse> handlePromptTemplate(
            PromptTemplateException ex, WebRequest request) {
        log.error("Prompt template error: {}", ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "PROMPT_TEMPLATE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ErrorResponse> handleCacheOperation(
            CacheOperationException ex, WebRequest request) {
        log.error("Cache operation error: {}", ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(
            InvalidRequestException ex, WebRequest request) {
        log.error("Invalid request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getErrorCode(),
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
        log.error("Validation error: {}", errors);
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "AI_007",
                "Validation failed", request, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", "AUTH_007",
                "Access denied", request, null);
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiService(
            AiServiceException ex, WebRequest request) {
        log.error("AI Service error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "AI_SERVICE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "AI_007",
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "AI_999", "An unexpected error occurred. Please try again later.",
                request, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, String errorCode,
            String message, WebRequest request, Map<String, String> validationErrors) {
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