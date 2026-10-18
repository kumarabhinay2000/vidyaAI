package org.vidyaai.aiservices.constant;

public final class RedisTTL {

    private RedisTTL() {
    }

    public static final long SUMMARY_SECONDS = 604800L;   // 7 days
    public static final long NOTES_SECONDS = 604800L;     // 7 days
    public static final long QUIZ_SECONDS = 604800L;      // 7 days
    public static final long CHATBOT_SECONDS = 86400L;    // 1 day
    public static final long TRANSCRIPT_SECONDS = 2592000L; // 30 days
    public static final long STATUS_SECONDS = 3600L;      // 1 hour
}