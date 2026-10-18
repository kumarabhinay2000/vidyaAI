package org.vidyaai.aiservices.constant;

public final class CacheKeys {

    private CacheKeys() {
    }

    private static final String PREFIX = "ai:";
    private static final String SEPARATOR = ":";

    public static String summary(String videoId, String type, String language) {
        return PREFIX + "summary" + SEPARATOR + videoId + SEPARATOR + type + SEPARATOR + language;
    }

    public static String notes(String videoId, String language) {
        return PREFIX + "notes" + SEPARATOR + videoId + SEPARATOR + language;
    }

    public static String quiz(String videoId, String difficulty, String type) {
        return PREFIX + "quiz" + SEPARATOR + videoId + SEPARATOR + difficulty + SEPARATOR + type;
    }

    public static String chatbot(String videoId, String userId, String hash) {
        return PREFIX + "chat" + SEPARATOR + videoId + SEPARATOR + userId + SEPARATOR + hash;
    }

    public static String transcript(String videoId) {
        return PREFIX + "transcript" + SEPARATOR + videoId;
    }

    public static String status(String requestId) {
        return PREFIX + "status" + SEPARATOR + requestId;
    }

    public static String usagePattern(String provider) {
        return PREFIX + "usage" + SEPARATOR + provider + SEPARATOR + "*";
    }
}