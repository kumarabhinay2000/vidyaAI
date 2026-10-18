package org.vidyaai.quizservices.constant;

public final class CacheKeys {

    private CacheKeys() {
    }

    private static final String PREFIX = "quiz:";
    private static final String SEPARATOR = ":";

    public static String quiz(String quizId) {
        return PREFIX + "meta" + SEPARATOR + quizId;
    }

    public static String quizByVideo(String videoId) {
        return PREFIX + "video" + SEPARATOR + videoId;
    }

    public static String analytics(String quizId) {
        return PREFIX + "analytics" + SEPARATOR + quizId;
    }

    public static String leaderboard() {
        return PREFIX + "leaderboard";
    }

    public static String userStats(String userId) {
        return PREFIX + "user-stats" + SEPARATOR + userId;
    }

    public static String attemptCooldown(String userId, String quizId) {
        return PREFIX + "cooldown" + SEPARATOR + userId + SEPARATOR + quizId;
    }
}