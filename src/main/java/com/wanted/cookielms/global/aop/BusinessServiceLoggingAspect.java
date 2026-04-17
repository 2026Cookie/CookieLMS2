package com.wanted.cookielms.global.aop;

import com.wanted.cookielms.global.logging.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.service.BusinessServiceLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BusinessServiceLoggingAspect {

    private final BusinessServiceLogService businessServiceLogService;

    @Around("@annotation(com.wanted.cookielms.global.aop.ServiceLogging)")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String classMethod = className + "." + methodName;

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            BusinessServiceLogEntity log = new BusinessServiceLogEntity();
            log.setServiceName(className);
            log.setMethodName(methodName);
            log.setClassMethod(classMethod);
            log.setExecutionTimeMs(executionTime);
            log.setIsSuccess(true);
            log.setUserId(getCurrentUserId());
            log.setTraceId(getTraceId());

            businessServiceLogService.saveAsync(log);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            BusinessServiceLogEntity log = new BusinessServiceLogEntity();
            log.setServiceName(className);
            log.setMethodName(methodName);
            log.setClassMethod(classMethod);
            log.setExecutionTimeMs(executionTime);
            log.setIsSuccess(false);
            log.setUserId(getCurrentUserId());
            log.setTraceId(getTraceId());

            businessServiceLogService.saveAsync(log);

            throw e;
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    // UserDetails에서 userId를 가져오는 로직
                    // 실제 구현은 프로젝트의 CustomUserDetailsService 구조에 맞게 수정 필요
                    return null;
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }

    private String getTraceId() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return (String) attributes.getRequest().getAttribute("traceId");
            }
        } catch (Exception e) {
            log.debug("Failed to get trace ID", e);
        }
        return null;
    }
}