package org.vidyaai.aiservices.exception;

public class VectorStoreException extends AiServiceException {

    public VectorStoreException(String message) {
        super(message, "AI_009");
    }

    public VectorStoreException(String message, Throwable cause) {
        super(message, "AI_009", cause);
    }
}
