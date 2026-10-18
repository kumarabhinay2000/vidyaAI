package org.vidyaai.videoservices.constant;

public final class RedisTTL {

    private RedisTTL() {
    }

    public static final long VIDEO_SECONDS = 3600L;
    public static final long METADATA_SECONDS = 3600L;
    public static final long STATUS_SECONDS = 300L;
    public static final long USER_VIDEOS_SECONDS = 900L;
}