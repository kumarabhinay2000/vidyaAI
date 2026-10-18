package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.exception.InvalidRequestException;

import java.util.UUID;

@Component
@Slf4j
public class ValidationUtil {

    public void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " cannot be null");
        }
    }

    public void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(fieldName + " cannot be blank");
        }
    }

    public void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new InvalidRequestException(
                    fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public void requireMinLength(String value, int minLength, String fieldName) {
        if (value != null && value.length() < minLength) {
            throw new InvalidRequestException(
                    fieldName + " must be at least " + minLength + " characters");
        }
    }

    public void requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new InvalidRequestException(
                    fieldName + " must be between " + min + " and " + max);
        }
    }

    public UUID parseUuid(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(fieldName + " cannot be blank");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(fieldName + " is not a valid UUID");
        }
    }
}