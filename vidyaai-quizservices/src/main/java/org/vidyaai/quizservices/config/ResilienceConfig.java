package org.vidyaai.quizservices.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfigCustomizer redisCacheCustomizer() {
        log.info("Registering Redis cache circuit breaker config");
        return CircuitBreakerConfigCustomizer.of("redisCache", builder -> builder
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(5));
    }

    @Bean
    public CircuitBreakerConfigCustomizer kafkaProducerCustomizer() {
        return CircuitBreakerConfigCustomizer.of("kafkaProducer", builder -> builder
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3));
    }
}