package org.vidyaai.aiservices.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vidyaai.aiservices.exception.LlmProviderException;
import org.vidyaai.aiservices.exception.LlmRateLimitException;
import org.vidyaai.aiservices.exception.LlmTimeoutException;

import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfigCustomizer groqChatCircuitBreakerCustomizer() {
        log.info("Registering custom circuit breaker config for Groq chat");
        return CircuitBreakerConfigCustomizer.of("groqChat", builder -> builder
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(LlmProviderException.class,
                        LlmTimeoutException.class,
                        LlmRateLimitException.class));
    }

    @Bean
    public CircuitBreakerConfigCustomizer groqEmbeddingCircuitBreakerCustomizer() {
        log.info("Registering custom circuit breaker config for Groq embedding");
        return CircuitBreakerConfigCustomizer.of("groqEmbedding", builder -> builder
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(5));
    }

    @SuppressWarnings("unused")
    private CircuitBreakerConfig unusedTypeAnchor() {
        return CircuitBreakerConfig.ofDefaults();
    }
}