package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.http.HttpStatus;

/**
 * 인증/권한 관련 예외
 */
public class AuthenticationException extends ApplicationException {

    // 로그인이 필요함
    public static AuthenticationException loginRequired() {
        return new AuthenticationException(HttpStatus.UNAUTHORIZED, "A001",
            "이 서비스는 로그인이 필요합니다.",
            ErrorSeverity.CRITICAL);
    }

    // 아이디/비번 불일치
    public static AuthenticationException loginFailed() {
        return new AuthenticationException(HttpStatus.UNAUTHORIZED, "A003",
            "아이디 또는 비밀번호가 일치하지 않습니다.",
            ErrorSeverity.CRITICAL);
    }

    // 계정 잠금
    public static AuthenticationException accountLocked(String userId) {
        return new AuthenticationException(HttpStatus.FORBIDDEN, "A004",
            String.format("사용자 %s의 계정이 비밀번호 5회 실패로 잠겼습니다.", userId),
            ErrorSeverity.CRITICAL);
    }

    public AuthenticationException(HttpStatus status, String code, String message, ErrorSeverity severity) {
        super(status, code, message, severity);
    }
}
