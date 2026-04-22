package com.wanted.cookielms.global.aop;

import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.businessService.service.BusinessServiceLogService;
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

    @Around("@annotation(com.wanted.cookielms.global.aop.BussinessServiceLogging)")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String classMethod = className + "." + methodName;

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            BusinessServiceLogEntity log = new BusinessServiceLogEntity();
            log.setClassMethod(classMethod);
            log.setExecutionTimeMs(executionTime);
            log.setIsSuccess(true);
            log.setTraceId(getTraceId());

            businessServiceLogService.saveBusinessServiceLog(log);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            BusinessServiceLogEntity log = new BusinessServiceLogEntity();
            log.setClassMethod(classMethod);
            log.setExecutionTimeMs(executionTime);
            log.setIsSuccess(false);
            log.setTraceId(getTraceId());

            businessServiceLogService.saveBusinessServiceLog(log);

            throw e;
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof com.wanted.cookielms.domain.auth.dto.AuthDetails authDetails) {
                    return authDetails.getLoginUserDTO().getUserId();  // ✅ 실제 userId 반환
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }

    private String getTraceId() {
        try {
            // ✅ MDC에서 직접 가져오기 (TraceIdFilter에서 저장한 곳)
            return org.slf4j.MDC.get("traceId");
        } catch (Exception e) {
            log.debug("Failed to get trace ID", e);
        }
        return null;
    }
}