package org.vidyaai.aiservices.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Value("${ai.processing.async-core-pool-size:5}")
    private int corePoolSize;

    @Value("${ai.processing.async-max-pool-size:20}")
    private int maxPoolSize;

    @Value("${ai.processing.async-queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = {"aiTaskExecutor", "aiTaskExecuter"})
    public Executor aiTaskExecutor() {
        log.info("Configuration AI async executor: core={}, max={}, queue={}",
                corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ai-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}