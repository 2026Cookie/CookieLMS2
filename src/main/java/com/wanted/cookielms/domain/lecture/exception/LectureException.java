package com.wanted.cookielms.domain.lecture.exception;

import com.wanted.cookielms.global.error.handler.ApplicationException;

public class LectureException extends ApplicationException {

    public LectureException(LectureErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errorCode.getSeverity());
    }
}
