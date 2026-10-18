package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenCounter {

    /**
     * Heuristic: ~4 characters per token for English text.
     * This is a lightweight estimation for cost/tracking purposes.
     */
    private static final int CHARS_PER_TOKEN = 4;

    public int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            return Math.max(1, text.length() / CHARS_PER_TOKEN);
        } catch (Exception e) {
            log.warn("Failed to estimate tokens: {}", e.getMessage());
            return 0;
        }
    }

    public int estimateTokensFromMessages(String... messages) {
        if (messages == null || messages.length == 0) {
            return 0;
        }
        int total = 0;
        for (String message : messages) {
            total += estimateTokens(message);
        }
        return total;
    }

    public boolean exceedsLimit(String text, int maxTokens) {
        return estimateTokens(text) > maxTokens;
    }
}