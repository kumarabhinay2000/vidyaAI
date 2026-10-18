package org.vidyaai.aiservices.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "groq.api")
@Data
@Slf4j
public class GroqConfig {

    private String key;
    private String baseUrl;
    private String model;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private Double temperature;

    public void logConfiguration() {
        log.info("Groq configuration loaded: baseUrl={}, model={}, timeout={}s, retries={}, temp={}",
                baseUrl, model, timeoutSeconds, maxRetries, temperature);
    }
}