package org.vidyaai.quizservices.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceMonitoringAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("within(org.vidyaai.quizservices.service..*)")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object timeServiceCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Object result = joinPoint.proceed();
            sample.stop(meterRegistry.timer("quiz_service.duration",
                    "class", className, "method", methodName, "status", "success"));
            return result;
        } catch (Throwable t) {
            sample.stop(meterRegistry.timer("quiz_service.duration",
                    "class", className, "method", methodName, "status", "error"));
            throw t;
        }
    }
}