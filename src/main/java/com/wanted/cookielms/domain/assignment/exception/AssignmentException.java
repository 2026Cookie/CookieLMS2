package com.wanted.cookielms.domain.assignment.exception;

import com.wanted.cookielms.global.error.handler.ApplicationException;

public class AssignmentException extends ApplicationException {

    public AssignmentException(AssignmentErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }
}
