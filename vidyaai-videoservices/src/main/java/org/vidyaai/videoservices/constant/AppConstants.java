package org.vidyaai.videoservices.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // API
    public static final String API_BASE_PATH = "/api/videos";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Error codes
    public static final String ERROR_VIDEO_NOT_FOUND = "VID_001";
    public static final String ERROR_INVALID_REQUEST = "VID_002";
    public static final String ERROR_FILE_TOO_LARGE = "VID_003";
    public static final String ERROR_UNSUPPORTED_FORMAT = "VID_004";
    public static final String ERROR_STORAGE_FAILURE = "VID_005";
    public static final String ERROR_YOUTUBE_FETCH = "VID_006";
    public static final String ERROR_INVALID_YOUTUBE_URL = "VID_007";
    public static final String ERROR_UPLOAD_FAILED = "VID_008";
    public static final String ERROR_ACCESS_DENIED = "VID_009";
    public static final String ERROR_CACHE_OPERATION = "VID_010";
    public static final String ERROR_DUPLICATE_VIDEO = "VID_011";
    public static final String ERROR_INTERNAL = "VID_999";

    // Business limits
    public static final int MIN_DURATION_SECONDS = 30;
    public static final int MAX_DURATION_SECONDS = 7200;
    public static final long MAX_FILE_SIZE_BYTES = 2147483648L; // 2GB

    // YouTube URL patterns
    public static final String YOUTUBE_WATCH_PATTERN =
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/|youtube\\.com/shorts/)[\\w-]{11}.*$";
    public static final String YOUTUBE_VIDEO_ID_PATTERN = "[\\w-]{11}";
}