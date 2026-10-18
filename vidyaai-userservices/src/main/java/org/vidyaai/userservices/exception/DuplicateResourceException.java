package org.vidyaai.userservices.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final String resouceName;
    private final String fieldName;
    private final Object fieldValue;
    private final String errorCode;

    public DuplicateResourceException(String resouceName, String fieldName, Object fieldValue) {

        super(String.format("%s already exists with %s: %s", resouceName, fieldName, fieldValue));

        this.resouceName=resouceName;
        this.fieldName=fieldName;
        this.fieldValue=fieldValue;
        this.errorCode="DUPLICATE_RESOURCE";
    }

    public DuplicateResourceException(String resouceName, String fieldName, Object fieldValue, String errorCode) {

        super(String.format("%s already exists with %s: %s", resouceName, fieldName, fieldValue));

        this.resouceName=resouceName;
        this.fieldName=fieldName;
        this.fieldValue=fieldValue;
        this.errorCode=errorCode;
    }

}