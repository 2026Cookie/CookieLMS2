package com.wanted.cookielms.domain.user.exception;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {

    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "USR001", "이미 존재하는 아이디입니다.", ErrorSeverity.INFO),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USR002", "이미 존재하는 이메일입니다.", ErrorSeverity.INFO),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR003", "사용자를 찾을 수 없습니다.", ErrorSeverity.WARNING),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USR004", "비밀번호가 일치하지 않습니다.", ErrorSeverity.INFO),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USR005", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.", ErrorSeverity.INFO);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}
