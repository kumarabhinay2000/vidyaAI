package org.vidyaai.quizservices.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    // Consumed
    public static final String AI_PROCESSING_COMPLETED = "vidya.ai.processing.completed";
    public static final String AI_PROCESSING_FAILED = "vidya.ai.processing.failed";

    // Produced
    public static final String QUIZ_ATTEMPTED = "vidya.quiz.attempted";
    public static final String QUIZ_COMPLETED = "vidya.quiz.completed";

    // Consumer groups
    public static final String QUIZ_SERVICE_GROUP = "quiz-service-group";
}