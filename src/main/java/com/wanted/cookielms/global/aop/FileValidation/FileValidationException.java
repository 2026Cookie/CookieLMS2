package com.wanted.cookielms.global.aop.FileValidation;

import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.http.HttpStatus;

public class FileValidationException extends ApplicationException {

    public FileValidationException(FileValidationErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }
}