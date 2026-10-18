package org.vidyaai.videoservices.constant;

public final class KafkaTopics {

    private KafkaTopics() {
        // Private constructor to prevent instantiation
    }

    public static final String VIDEO_UPLOADED = "vidya.video.uploaded";
    public static final String VIDEO_UPDATED = "vidya.video.updated";
    public static final String VIDEO_DELETED = "vidya.video.deleted";
    public static final String AI_PROCESSING_COMPLETED = "vidya.ai.processing.completed";
    public static final String AI_PROCESSING_FAILED = "vidya.ai.processing.failed";
    public static final String VIDEO_SERVICE_GROUP = "video-service-group";
}
