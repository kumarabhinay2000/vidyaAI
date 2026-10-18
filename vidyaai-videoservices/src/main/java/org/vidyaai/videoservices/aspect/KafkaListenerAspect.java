package org.vidyaai.videoservices.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class KafkaListenerAspect {

    private static final String TRACE_ID = "traceId";

    @Pointcut("within(org.vidyaai.videoservices.listener..*)")
    public void listenerMethods() {
    }

    @Around("listenerMethods()")
    public Object traceListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        String method = joinPoint.getSignature().toShortString();
        log.info("→ Kafka Listener: {} traceId={}", method, traceId);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("← Kafka Listener: {} completed in {}ms traceId={}",
                    method, System.currentTimeMillis() - start, traceId);
            return result;
        } catch (Throwable t) {
            log.error("✖ Kafka Listener: {} failed in {}ms traceId={} - {}",
                    method, System.currentTimeMillis() - start, traceId, t.getMessage(), t);
            throw t;
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}