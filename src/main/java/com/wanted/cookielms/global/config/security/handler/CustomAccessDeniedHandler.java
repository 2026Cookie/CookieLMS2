package com.wanted.cookielms.global.config.security.handler;

import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

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

        String traceId = UUID.randomUUID().toString();

        // 💡 사용자님이 주신 WebGlobalExceptionHandler의 saveErrorLog 로직과 동일하게 구성
        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode("FORBIDDEN") // 403
                .errorMessage("접근 권한이 없습니다: " + accessDeniedException.getMessage())
                .exceptionName(accessDeniedException.getClass().getSimpleName())
                .requestUri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .clientIp(getClientIp(request))
                .userId("ANONYMOUS") // 추후 Principal 연동 가능
                .stackTrace("Security Filter Level: Access Denied")
                .traceId(traceId)
                .severity(ErrorSeverity.CRITICAL) // 💡 Service 로직에 따라 DB에 저장됨
                .build();

        // 비동기 서비스 호출
        errorLogService.saveErrorLogAsync(errorLog);

        // 예쁜 HTML 화면(CustomErrorController)으로 리다이렉트
        response.sendRedirect("/error?status=403&traceId=" + traceId);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }
}