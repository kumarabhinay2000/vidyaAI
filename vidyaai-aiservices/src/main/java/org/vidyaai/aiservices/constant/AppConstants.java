package org.vidyaai.aiservices.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // API
    public static final String API_BASE_PATH = "/api/ai";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Error codes
    public static final String ERROR_AI_SERVICE = "AI_001";
    public static final String ERROR_LLM_PROVIDER = "AI_002";
    public static final String ERROR_LLM_TIMEOUT = "AI_003";
    public static final String ERROR_LLM_RATE_LIMIT = "AI_004";
    public static final String ERROR_TRANSCRIPT_EXTRACTION = "AI_005";
    public static final String ERROR_RESOURCE_NOT_FOUND = "AI_006";
    public static final String ERROR_INVALID_REQUEST = "AI_007";
    public static final String ERROR_CACHE_OPERATION = "AI_008";
    public static final String ERROR_VECTOR_STORE = "AI_009";
    public static final String ERROR_PROMPT_TEMPLATE = "AI_010";
    public static final String ERROR_INTERNAL = "AI_999";

    // Business limits
    public static final int MAX_SUMMARY_POINTS = 20;
    public static final int MIN_SUMMARY_POINTS = 3;
    public static final int MAX_QUIZ_QUESTIONS = 20;
    public static final int MIN_QUIZ_QUESTIONS = 3;
    public static final int MAX_CHAT_MESSAGE_LENGTH = 2000;
    public static final int MAX_CHAT_HISTORY_SIZE = 10;

    // Retry / limits
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final long DEFAULT_TIMEOUT_SECONDS = 30L;

    // Provider
    public static final String DEFAULT_LLM_PROVIDER = "GROQ";
}