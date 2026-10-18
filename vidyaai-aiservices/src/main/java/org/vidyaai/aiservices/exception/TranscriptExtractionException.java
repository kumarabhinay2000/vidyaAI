package org.vidyaai.aiservices.exception;

public class TranscriptExtractionException extends AiServiceException {

    public TranscriptExtractionException(String message) {
        super(message, "AI_005");
    }

    public TranscriptExtractionException(String message, Throwable cause) {
        super(message, "AI_005", cause);
    }
}