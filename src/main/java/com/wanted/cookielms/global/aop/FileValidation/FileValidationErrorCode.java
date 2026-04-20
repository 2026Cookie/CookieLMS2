package com.wanted.cookielms.global.aop.FileValidation;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileValidationErrorCode {

    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FV001", "파일 용량을 초과했습니다.", ErrorSeverity.CRITICAL),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "FV002", "지원하지 않는 파일 형식입니다.", ErrorSeverity.INFO),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "FV003", "파일 형식이 올바르지 않습니다.", ErrorSeverity.INFO);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}