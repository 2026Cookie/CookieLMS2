package com.wanted.cookielms.global.config.security.handler;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.global.error.handler.GlobalErrorCode;
import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // ✅ SecurityContextHolder에서 userId 추출
        GlobalErrorCode globalErrorCode = GlobalErrorCode.FORBIDDEN;

        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode(globalErrorCode.getCode())
                .errorMessage(globalErrorCode.getMessage() + ": " + accessDeniedException.getMessage())
                .exceptionName(accessDeniedException.getClass().getSimpleName())
                .clientIp(getClientIp(request))
                .stackTrace("Security Filter Level: Access Denied")
                .traceId(traceId)
                .userId(getCurrentUserId())
                .severity(globalErrorCode.getSeverity())
                .build();

        errorLogService.saveErrorLogAsync(errorLog);

        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, globalErrorCode.getStatus().value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, globalErrorCode.getMessage());
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/error");
        dispatcher.forward(request, response);
    }

    // ✅ SecurityContextHolder에서 userId 추출
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() &&
                    !"anonymousUser".equals(auth.getPrincipal())) {

                AuthDetails authDetails = (AuthDetails) auth.getPrincipal();
                return authDetails.getLoginUserDTO().getUserId();
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }
}