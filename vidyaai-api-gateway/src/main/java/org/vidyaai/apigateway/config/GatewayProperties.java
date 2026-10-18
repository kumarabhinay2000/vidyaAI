package org.vidyaai.apigateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "gateway")
@Data
@Slf4j
public class GatewayProperties {

    private Security security = new Security();
    private RateLimit rateLimit = new RateLimit();
    private Correlation correlation = new Correlation();
    private Timeouts timeouts = new Timeouts();

    @Data
    public static class Security {
        private Jwt jwt = new Jwt();
        private List<String> publicPaths = new ArrayList<>();

        @Data
        public static class Jwt {
            private String secret;
        }
    }

    @Data
    public static class RateLimit {
        private boolean enabled = true;
        private int defaultRequestsPerMinute = 100;
        private int authRequestsPerMinute = 20;
    }

    @Data
    public static class Correlation {
        private String headerName = "X-Correlation-Id";
    }

    @Data
    public static class Timeouts {
        private long connectMs = 5000L;
        private long responseMs = 30000L;
    }
}