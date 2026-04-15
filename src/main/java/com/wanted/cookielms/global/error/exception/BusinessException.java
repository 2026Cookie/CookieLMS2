package com.wanted.cookielms.global.error.exception;


import com.wanted.cookielms.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        // 부모 클래스인 RuntimeException에 메시지를 넘겨주어, 서버 로그(스택 트레이스)에서
        // 에러 원인을 쉽게 파악할 수 있도록 합니다.
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
