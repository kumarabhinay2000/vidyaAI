package org.vidyaai.aiservices.enums;

public enum ProcessingStatus {
    PENDING,
    EXTRACTING_TRANSCRIPT,
    GENERATING_SUMMARY,
    GENERATING_NOTES,
    GENERATING_QUIZ,
    GENERATING_EMBEDDINGS,
    COMPLETED,
    FAILED
}