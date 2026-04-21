package com.wanted.cookielms.global.error.handler;

/**
 * alert 메시지로 던지고 ErrorLog에 기록되는 예외.
 * 핸들러는 메시지 텍스트만 응답 body로 반환한다.
 */
public class AlertException extends ApplicationException {

    public AlertException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AlertException(ErrorCode errorCode, String customMessage) {
        super(errorCode.getStatus(), errorCode.getCode(), customMessage, errorCode.getSeverity());
    }
}
