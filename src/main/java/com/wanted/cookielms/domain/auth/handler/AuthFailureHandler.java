package com.wanted.cookielms.domain.auth.handler;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ErrorLogService errorLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        String loginId = request.getParameter("loginId");
        String errorMessage = determineErrorMessage(exception);

        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .errorCode("AUTH_FAILURE")
                .errorMessage("로그인 실패 (" + loginId + "): " + errorMessage)
                .exceptionName(exception.getClass().getSimpleName())
                .clientIp(getClientIp(request))
                .stackTrace(exception.getMessage())
                .traceId(traceId)
                .severity(ErrorSeverity.WARNING)
                .build();

        errorLogService.saveErrorLogAsync(errorLog);

        // 세션에 에러 메시지 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginError", errorMessage);
        setDefaultFailureUrl("/user/login?error");

        super.onAuthenticationFailure(request, response, exception);
    }

    private String determineErrorMessage(AuthenticationException exception) {
        log.error("로그인 실패 상세 원인 (Exception): {}", exception.getClass().getName());
        log.error("로그인 실패 메시지 (Message): {}", exception.getMessage());
        if (exception.getCause() != null) {
            log.error("Root Cause: {}", exception.getCause().getMessage());
        }

        if (exception instanceof LockedException) {
            return "비정상적인 활동으로 밴당한 회원입니다. 고객센터에 문의해주세요. 고객센터 : 국번 없이 111";
        } else if (exception instanceof DisabledException) {
            return "탈퇴한 회원입니다.";
        } else if (exception instanceof BadCredentialsException) {
            return "비밀번호가 맞지 않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            return "등록된 회원 정보가 없습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            if (exception.getCause() instanceof UsernameNotFoundException) {
                return "등록된 회원 정보가 없습니다.";
            }
            return "내부 시스템 문제로 인해 요청을 처리할 수 없습니다.";
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