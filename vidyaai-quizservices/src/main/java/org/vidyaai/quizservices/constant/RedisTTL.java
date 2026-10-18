package org.vidyaai.quizservices.constant;

public final class RedisTTL {

    private RedisTTL() {
    }

    public static final long QUIZ_SECONDS = 3600L;
    public static final long ANALYTICS_SECONDS = 900L;
    public static final long LEADERBOARD_SECONDS = 300L;
    public static final long USER_STATS_SECONDS = 600L;
    public static final long COOLDOWN_SECONDS = 300L;
}