package com.wanted.cookielms.global.aop.FileValidation;

import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.error.handler.ErrorCode;
import lombok.Getter;

@Getter
public class FileValidationException extends ApplicationException {

    private final String redirectUrl; // null이면 history.back() 동작

    public FileValidationException(ErrorCode errorCode) {
        super(errorCode);
        this.redirectUrl = null;
    }

    public FileValidationException(ErrorCode errorCode, String redirectUrl) {
        super(errorCode);
        this.redirectUrl = redirectUrl;
    }

    public FileValidationException(ErrorCode errorCode, String customMessage, String redirectUrl) {
        super(errorCode.getStatus(), errorCode.getCode(), customMessage, errorCode.getSeverity());
        this.redirectUrl = redirectUrl;
    }
}
