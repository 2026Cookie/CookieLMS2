package com.wanted.cookielms.domain.enrollment.exception;

import com.wanted.cookielms.global.error.handler.AlertException;

public class EnrollmentException extends AlertException {

    public EnrollmentException(EnrollmentErrorCode errorCode) {
        super(errorCode);
    }

    public EnrollmentException(EnrollmentErrorCode errorCode, String redirectUrl) {
        super(errorCode, redirectUrl);
    }
}
