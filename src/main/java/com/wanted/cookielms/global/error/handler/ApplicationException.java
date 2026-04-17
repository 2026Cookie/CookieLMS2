package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 예외의 기본 클래스
 * 직접 생성하거나 상속받아 사용할 수 있음
 */
@Getter
public class ApplicationException extends RuntimeException {

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

    /**
     * ErrorCode 열거형을 이용한 생성자
     */
    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.severity = errorCode.getSeverity();
    }
}