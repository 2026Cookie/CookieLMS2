package com.wanted.cookielms.global.config.security.handler;

import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

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

        String traceId = UUID.randomUUID().toString();

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

        // 로그인 페이지로 보내거나, 예쁜 에러 화면으로 전송
        response.sendRedirect("/user/login?error=unauthorized&traceId=" + traceId);
    }
}