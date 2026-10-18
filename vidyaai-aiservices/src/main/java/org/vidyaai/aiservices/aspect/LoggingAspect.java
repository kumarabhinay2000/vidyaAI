package org.vidyaai.aiservices.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(org.vidyaai.aiservices.controller..*)")
    public void controllerMethods() {
    }

    @Pointcut("within(org.vidyaai.aiservices.service..*)")
    public void serviceMethods() {
    }

    @Pointcut("within(org.vidyaai.aiservices.client..*)")
    public void clientMethods() {
    }

    @Around("controllerMethods()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("→ Controller: {} args={}", method, safeArgs(joinPoint.getArgs()));
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("← Controller: {} completed in {}ms",
                    method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("✖ Controller: {} failed in {}ms - {}",
                    method, System.currentTimeMillis() - start, t.getMessage(), t);
            throw t;
        }
    }

    @Around("serviceMethods()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.debug("→ Service: {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("← Service: {} completed in {}ms",
                    method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("✖ Service: {} failed in {}ms - {}",
                    method, System.currentTimeMillis() - start, t.getMessage(), t);
            throw t;
        }
    }

    @Around("clientMethods()")
    public Object logClient(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.debug("→ Client: {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("← Client: {} completed in {}ms",
                    method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("✖ Client: {} failed in {}ms - {}",
                    method, System.currentTimeMillis() - start, t.getMessage(), t);
            throw t;
        }
    }

    private String safeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            return Arrays.toString(Arrays.stream(args)
                    .map(this::truncate)
                    .toArray());
        } catch (Exception e) {
            return "[unavailable]";
        }
    }

    private String truncate(Object o) {
        if (o == null) {
            return "null";
        }
        String s = o.toString();
        return s.length() > 200 ? s.substring(0, 200) + "..." : s;
    }
}