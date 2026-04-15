package com.wanted.cookielms.global.error.handler;

/**
 * 비즈니스 로직 수행 중 발생하는 예외
 */
public class BusinessException extends ApplicationException {

    // 미리 정의된 ErrorCode를 사용하는 경우
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }

    // 상황에 따라 메시지를 동적으로 던지고 싶을 때
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(errorCode.getStatus(), errorCode.getCode(), customMessage, errorCode.getSeverity());
    }
}