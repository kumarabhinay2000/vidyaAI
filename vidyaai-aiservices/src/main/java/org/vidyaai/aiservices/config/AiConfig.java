package org.vidyaai.aiservices.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
@Slf4j
public class AiConfig {

    private Processing processing = new Processing();
    private Cache cache = new Cache();
    private Limits limits = new Limits();

    @Data
    public static class Processing {
        private Integer chunkSize = 2000;
        private Integer chunkOverlap = 200;
        private Integer maxContentLength = 50000;
        private Integer asyncCorePoolSize = 5;
        private Integer asyncMaxPoolSize = 20;
        private Integer asyncQueueCapacity = 100;
    }

    @Data
    public static class Cache {
        private Long summaryTtlSeconds = 604800L;
        private Long notesTtlSeconds = 604800L;
        private Long quizTtlSeconds = 604800L;
        private Long chatbotTtlSeconds = 86400L;
        private Long transcriptTtlSeconds = 2592000L;
    }

    @Data
    public static class Limits {
        private Integer maxChatMessageLength = 2000;
        private Integer maxChatHistorySize = 10;
        private Integer maxVideoDurationSeconds = 7200;
        private Integer minVideoDurationSeconds = 30;
    }
}