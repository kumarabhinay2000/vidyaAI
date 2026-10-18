package org.vidyaai.userservices.model.constants;

public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String USER_ID_CLAIM = "userId";
    public static final String EMAIL_CLAIM = "email";
    public static final String ROLE_CLAIM = "roles";
    public static final String PLAN_CLAIM = "plan";

    // Redis Keys
    public static final String REDIS_USER_PREFIX = "user:";
    public static final String REDIS_SESSION_PREFIX = "session:";
    public static final String REDIS_RATE_LIMIT_PREFIX = "ratelimit:";
    public static final String REDIS_REFRESH_TOKEN_PREFIX = "refresh:";

    // Kafka Topics
    public static final String TOPIC_USER_REGISTERED = "vidya.user.registered";
    public static final String TOPIC_USER_LOGIN = "vidya.user.login";
    public static final String TOPIC_USER_UPDATED = "vidya.user.updated";
    public static final String TOPIC_USER_DELETED = "vidya.user.deleted";
    public static final String TOPIC_SUBSCRIPTION_CHANGED = "vidya.subscription.changed";

    // Cache TTLs (in seconds)
    public static final long CACHE_USER_TTL = 3600; // 1 hour
    public static final long CACHE_SESSION_TTL = 86400; // 24 hours
    public static final long CACHE_REFRESH_TOKEN_TTL = 604800; // 7 days

    // Rate Limiting
    public static final int DEFAULT_RATE_LIMIT = 100;
    public static final int AUTH_RATE_LIMIT = 20;

    // Error Codes
    public static final String ERROR_USER_NOT_FOUND = "USER_001";
    public static final String ERROR_EMAIL_EXISTS = "USER_002";
    public static final String ERROR_INVALID_CREDENTIALS = "AUTH_001";
    public static final String ERROR_INVALID_TOKEN = "AUTH_002";
    public static final String ERROR_TOKEN_EXPIRED = "AUTH_003";
    public static final String ERROR_INVALID_REFRESH_TOKEN = "AUTH_004";
    public static final String ERROR_RATE_LIMIT_EXCEEDED = "RATE_001";
    public static final String ERROR_INVALID_REQUEST = "REQ_001";
    public static final String ERROR_INTERNAL_SERVER = "SYS_001";
}