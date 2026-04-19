package com.wanted.cookielms.global.config.security.handler;

import com.wanted.cookielms.global.error.handler.GlobalErrorCode;
import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorLogService errorLogService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        GlobalErrorCode globalErrorCode = GlobalErrorCode.FORBIDDEN; // ← 추가

        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode(globalErrorCode.getCode()) // ← ErrorCode 사용
                .errorMessage(globalErrorCode.getMessage() + ": " + accessDeniedException.getMessage())
                .exceptionName(accessDeniedException.getClass().getSimpleName())
                .requestUri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .clientIp(getClientIp(request))
                .userId("ANONYMOUS")
                .stackTrace("Security Filter Level: Access Denied")
                .traceId(traceId)
                .severity(globalErrorCode.getSeverity()) // ← ErrorCode 사용
                .build();

        errorLogService.saveErrorLogAsync(errorLog);

        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, globalErrorCode.getStatus().value()); // ← 변경
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, globalErrorCode.getMessage()); // ← 변경
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/error");
        dispatcher.forward(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }
}