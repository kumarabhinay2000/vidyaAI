package org.vidyaai.aiservices.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    // Consumed
    public static final String VIDEO_UPLOADED = "vidya.video.uploaded";
    public static final String AI_REGENERATE = "vidya.ai.regenerate";

    // Produced
    public static final String AI_PROCESSING_COMPLETED = "vidya.ai.processing.completed";
    public static final String AI_PROCESSING_FAILED = "vidya.ai.processing.failed";

    // Consumer groups
    public static final String AI_SERVICE_GROUP = "ai-service-group";

    // Headers
    public static final String HEADER_EVENT_TYPE = "eventType";
    public static final String HEADER_CORRELATION_ID = "correlationId";
}