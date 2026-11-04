package com.fund.trade.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class AuditLogAspect {
    private final AuditLogRepository repository;

    public AuditLogAspect(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Around("@annotation(auditLog)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            save(auditLog.operation(), "SUCCESS", null, joinPoint, elapsedMs(startedAt));
            return result;
        } catch (Throwable exception) {
            save(auditLog.operation(), "FAILED", exception.getClass().getSimpleName(),
                    joinPoint, elapsedMs(startedAt));
            throw exception;
        }
    }

    private void save(String operation, String result, String errorType,
                      ProceedingJoinPoint joinPoint, long durationMs) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();
        String userId = request == null ? null : request.getHeader("X-User-Id");
        String uri = request == null ? "internal" : request.getRequestURI();
        String summary = Arrays.stream(joinPoint.getArgs())
                .map(argument -> argument == null ? "null" : argument.getClass().getSimpleName())
                .collect(Collectors.joining(",", "[", "]"));
        repository.save(new AuditLogRecord(operation, userId, result, summary,
                errorType, durationMs, uri));
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
