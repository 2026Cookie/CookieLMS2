package com.wanted.cookielms.domain.enrollment.exception;

import com.wanted.cookielms.global.error.handler.ApplicationException;

public class EnrollmentException extends ApplicationException {

    public EnrollmentException(EnrollmentErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }
}