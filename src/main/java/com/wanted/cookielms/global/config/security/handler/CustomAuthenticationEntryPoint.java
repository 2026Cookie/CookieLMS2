package com.wanted.cookielms.global.config.security.handler;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErrorLogService errorLogService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode("UNAUTHORIZED") // 401
                .errorMessage("로그인이 필요합니다: " + authException.getMessage())
                .exceptionName(authException.getClass().getSimpleName())
                .requestUri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .clientIp(request.getRemoteAddr())
                .userId("ANONYMOUS")
                .stackTrace("Security Filter Level: Unauthorized")
                .traceId(traceId)
                .severity(ErrorSeverity.WARNING) // 💡 Service 로직에 따라 파일 로그([FILE LOG])에만 남음
                .build();

        errorLogService.saveErrorLogAsync(errorLog);

// 세션에 에러 메시지 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginError", "로그인이 필요합니다");

        response.sendRedirect("/user/login");
    }
}