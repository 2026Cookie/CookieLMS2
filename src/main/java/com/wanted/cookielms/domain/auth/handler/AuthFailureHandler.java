package com.wanted.cookielms.domain.auth.handler;

import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ErrorLogService errorLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String traceId = UUID.randomUUID().toString();
        String loginId = request.getParameter("loginId"); // SecurityConfig에서 설정한 파라미터명

        // 1. 예외 유형에 따른 사용자 메시지 결정
        String errorMessage = determineErrorMessage(exception);

        // 2. [로깅] 우리 프로젝트 표준에 맞게 ErrorLogEntity 생성 및 비동기 저장
        // 일반적인 로그인 실패는 서비스 로직에 따라 파일 로그([FILE LOG])에만 남도록 WARNING 부여
        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode("AUTH_FAILURE")
                .errorMessage("로그인 실패 (" + loginId + "): " + errorMessage)
                .exceptionName(exception.getClass().getSimpleName())
                .requestUri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .clientIp(getClientIp(request))
                .userId(loginId != null ? loginId : "UNKNOWN")
                .stackTrace(exception.getMessage())
                .traceId(traceId)
                .severity(ErrorSeverity.WARNING)
                .build();

        errorLogService.saveErrorLogAsync(errorLog);

        // 3. 메시지를 쿼리 파라미터로 담아 로그인 페이지로 리다이렉트
        // 한글 깨짐 방지를 위해 인코딩 필수
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        setDefaultFailureUrl("/user/login?error=true&message=" + encodedMessage + "&traceId=" + traceId);

        super.onAuthenticationFailure(request, response, exception);
    }

    private String determineErrorMessage(AuthenticationException exception) {
        // 💡 실제 어떤 예외인지 콘솔에 찍어서 확인합니다.
        log.error("로그인 실패 상세 원인 (Exception): {}", exception.getClass().getName());
        log.error("로그인 실패 메시지 (Message): {}", exception.getMessage());
        if (exception.getCause() != null) {
            log.error("Root Cause: {}", exception.getCause().getMessage());
        }

        if (exception instanceof BadCredentialsException) {
            return "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            return "내부 시스템 문제로 인해 요청을 처리할 수 없습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            return "존재하지 않는 계정입니다.";
        }
        return "알 수 없는 이유로 로그인에 실패하였습니다. (에러타입: " + exception.getClass().getSimpleName() + ")";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }
}