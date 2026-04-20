package com.wanted.cookielms.global.aop.FileValidation;

import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.error.handler.ErrorCode;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileValidationException extends ApplicationException {

    private final String redirectUrl; // null이면 history.back() 동작

    // =================================================================
    // 1. 문자열(String) 기반 생성자 (기존 유지 - 빠른 하드코딩용)
    // =================================================================

    // =================================================================
    // 2. 🌟 ErrorCode 인터페이스 기반 생성자 (다형성 적용)
    // 어떠한 도메인의 Enum(GlobalErrorCode, FileValidationErrorCode 등)도 모두 받을 수 있음
    // =================================================================

    /**
     * Enum 지정 생성자: 알림 띄우고 이전 페이지로 돌아가기
     */
    public FileValidationException(ErrorCode errorCode) {
        super(errorCode); // ApplicationException의 ErrorCode 생성자 호출
        this.redirectUrl = null;
    }

    /**
     * Enum + URL 지정 생성자: 알림 띄우고 특정 페이지로 이동하기
     */
    public FileValidationException(ErrorCode errorCode, String redirectUrl) {
        super(errorCode);
        this.redirectUrl = redirectUrl;
    }

    /**
     * Enum + 동적 메시지 + URL 지정 생성자:
     * Enum의 기본 메시지 대신 변수가 포함된 커스텀 메시지를 사용하고 싶을 때
     * (예: "[파일명.jpg]은 지원하지 않는 파일입니다.")
     */
    public FileValidationException(ErrorCode errorCode, String customMessage, String redirectUrl) {
        // 부모의 기본 생성자(상태, 코드, 메시지, 심각도)를 직접 호출하여 메시지만 바꿔치기
        super(errorCode.getStatus(), errorCode.getCode(), customMessage, errorCode.getSeverity());
        this.redirectUrl = redirectUrl;
    }
}