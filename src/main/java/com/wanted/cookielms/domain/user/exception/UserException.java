package com.wanted.cookielms.domain.user.exception;

import com.wanted.cookielms.global.error.handler.ApplicationException;

public class UserException extends ApplicationException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }
}
