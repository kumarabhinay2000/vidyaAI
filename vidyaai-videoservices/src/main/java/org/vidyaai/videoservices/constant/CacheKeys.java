package org.vidyaai.videoservices.constant;

public final class CacheKeys {

    private CacheKeys() {
    }

    private static final String PREFIX = "video:";
    private static final String SEPARATOR = ":";

    public static String video(String videoId) {
        return PREFIX + "meta" + SEPARATOR + videoId;
    }

    public static String videoByUrl(String sourceUrlHash) {
        return PREFIX + "url" + SEPARATOR + sourceUrlHash;
    }

    public static String status(String videoId) {
        return PREFIX + "status" + SEPARATOR + videoId;
    }

    public static String userVideos(String userId) {
        return PREFIX + "user" + SEPARATOR + userId;
    }
}