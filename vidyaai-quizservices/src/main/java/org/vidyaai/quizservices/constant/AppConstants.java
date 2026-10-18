package org.vidyaai.quizservices.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // API
    public static final String API_BASE_PATH = "/api/quizzes";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Error codes
    public static final String ERROR_QUIZ_NOT_FOUND = "QUIZ_001";
    public static final String ERROR_ATTEMPT_NOT_FOUND = "QUIZ_002";
    public static final String ERROR_INVALID_REQUEST = "QUIZ_003";
    public static final String ERROR_ALREADY_ATTEMPTED = "QUIZ_004";
    public static final String ERROR_MAX_ATTEMPTS_REACHED = "QUIZ_005";
    public static final String ERROR_QUIZ_EXPIRED = "QUIZ_006";
    public static final String ERROR_ACCESS_DENIED = "QUIZ_007";
    public static final String ERROR_CACHE_OPERATION = "QUIZ_008";
    public static final String ERROR_RESOURCE_NOT_FOUND = "QUIZ_009";
    public static final String ERROR_INTERNAL = "QUIZ_999";

    // Business limits
    public static final int DEFAULT_PASSING_PERCENTAGE = 70;
    public static final int MAX_ATTEMPTS_PER_QUIZ = 10;
    public static final int MAX_QUIZ_QUESTIONS = 100;
    public static final int MIN_QUIZ_QUESTIONS = 1;
    public static final int LEADERBOARD_MAX_ENTRIES = 100;
}