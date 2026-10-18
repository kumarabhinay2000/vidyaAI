package org.vidyaai.aiservices.exception;

public class PromptTemplateException extends AiServiceException {

    public PromptTemplateException(String message) {
        super(message, "AI_010");
    }

    public PromptTemplateException(String message, Throwable cause) {
        super(message, "AI_010", cause);
    }
}