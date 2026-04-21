package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    // ==========================================
    // HTTP 상태 코드별 에러 (GlobalExceptionHandler용)
    // 커스텀 예외들은 각자 자신의 code를 정의함
    // ==========================================
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다.", ErrorSeverity.INFO),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "인증이 필요합니다.", ErrorSeverity.CRITICAL),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403", "접근 권한이 없습니다.", ErrorSeverity.CRITICAL),
    // 🌟 수정 포인트 1: 세미콜론(;)을 콤마(,)로 변경
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 내부 오류가 발생했습니다.", ErrorSeverity.CRITICAL);


    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}