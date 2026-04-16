package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 예외의 기본 추상 클래스
 * 모든 커스텀 예외는 이 클래스를 상속받음
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final ErrorSeverity severity;

    public ApplicationException(HttpStatus status, String code, String message, ErrorSeverity severity) {
        super(message);
        this.status = status;
        this.code = code;
        this.severity = severity;
    }

    public ApplicationException(HttpStatus status, String code, String message) {
        this(status, code, message, ErrorSeverity.WARNING);
    }
}
