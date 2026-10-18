package org.vidyaai.userservices.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    private final String errorCode;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));

        this.resourceName=resourceName;
        this.fieldName=fieldName;
        this.fieldValue=fieldValue;
        this.errorCode="RESOURCE_NOT_FOUND";
    }
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue, String errorCode) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));

        this.resourceName=resourceName;
        this.fieldName=fieldName;
        this.fieldValue=fieldValue;
        this.errorCode=errorCode;
    }
}