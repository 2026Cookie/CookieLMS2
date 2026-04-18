package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==========================================
    // HTTP 상태 코드별 에러 (GlobalExceptionHandler용)
    // 커스텀 예외들은 각자 자신의 code를 정의함
    // ==========================================
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다.", ErrorSeverity.INFO),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "인증이 필요합니다.", ErrorSeverity.CRITICAL),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403", "접근 권한이 없습니다.", ErrorSeverity.CRITICAL),
    NOT_FOUND(HttpStatus.NOT_FOUND, "404", "요청한 리소스를 찾을 수 없습니다.", ErrorSeverity.WARNING),
    CONFLICT(HttpStatus.CONFLICT, "409", "요청이 현재 상태와 충돌합니다.", ErrorSeverity.WARNING),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 내부 오류가 발생했습니다.", ErrorSeverity.CRITICAL);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}