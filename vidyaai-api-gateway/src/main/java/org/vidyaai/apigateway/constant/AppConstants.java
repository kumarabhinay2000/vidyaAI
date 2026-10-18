package org.vidyaai.apigateway.constant;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String USER_PLAN_HEADER = "X-User-Plan";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    public static final String USER_ID_CLAIM = "userId";
    public static final String EMAIL_CLAIM = "email";
    public static final String ROLE_CLAIM = "roles";
    public static final String PLAN_CLAIM = "plan";

    public static final String ERROR_UNAUTHORIZED = "GW_001";
    public static final String ERROR_INVALID_TOKEN = "GW_002";
    public static final String ERROR_TOKEN_EXPIRED = "GW_003";
    public static final String ERROR_RATE_LIMIT = "GW_004";
    public static final String ERROR_SERVICE_UNAVAILABLE = "GW_005";
    public static final String ERROR_GATEWAY = "GW_999";
}