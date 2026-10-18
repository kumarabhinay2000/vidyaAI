package org.vidyaai.videoservices.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.vidyaai.videoservices.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVideoNotFound(
            VideoNotFoundException ex, WebRequest request) {
        log.error("Video not found: {}", ex.getMessage());
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

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(
            FileTooLargeException ex, WebRequest request) {
        log.error("File too large: {}", ex.getMessage());
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, WebRequest request) {
        log.error("Upload size exceeded: {}", ex.getMessage());
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "VID_003",
                "Uploaded file exceeds maximum allowed size", request, null);
    }

    @ExceptionHandler(UnsupportedFormatException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedFormat(
            UnsupportedFormatException ex, WebRequest request) {
        log.error("Unsupported format: {}", ex.getMessage());
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidYouTubeUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidYouTubeUrl(
            InvalidYouTubeUrlException ex, WebRequest request) {
        log.error("Invalid YouTube URL: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(YouTubeFetchException.class)
    public ResponseEntity<ErrorResponse> handleYouTubeFetch(
            YouTubeFetchException ex, WebRequest request) {
        log.error("YouTube fetch error: {}", ex.getMessage());
        return build(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(
            StorageException ex, WebRequest request) {
        log.error("Storage error: {}", ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(UploadFailedException.class)
    public ResponseEntity<ErrorResponse> handleUploadFailed(
            UploadFailedException ex, WebRequest request) {
        log.error("Upload failed: {}", ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_FAILED",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateVideoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateVideoException ex, WebRequest request) {
        log.warn("Duplicate video: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getErrorCode(),
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "VID_009",
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
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "VID_002",
                "Validation failed", request, errors);
    }

    @ExceptionHandler(CacheOperationException.class)
    public ResponseEntity<ErrorResponse> handleCacheOperation(
            CacheOperationException ex, WebRequest request) {
        log.error("Cache error: {}", ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(VideoServiceException.class)
    public ResponseEntity<ErrorResponse> handleVideoService(
            VideoServiceException ex, WebRequest request) {
        log.error("Video service error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "VIDEO_SERVICE_ERROR",
                ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "VID_002",
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "VID_999", "An unexpected error occurred.", request, null);
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