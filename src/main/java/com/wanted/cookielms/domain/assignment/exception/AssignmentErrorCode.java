package com.wanted.cookielms.domain.assignment.exception;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AssignmentErrorCode {

    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ASG001", "존재하지 않는 과제입니다.", ErrorSeverity.WARNING),
    SUBMISSION_DEADLINE_PASSED(HttpStatus.CONFLICT, "ASG002", "제출 기한이 지났습니다. (비정상 접근 감지)", ErrorSeverity.CRITICAL),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "ASG003", "PDF 형식의 파일만 제출 가능합니다.", ErrorSeverity.INFO),
    FILE_REQUIRED(HttpStatus.BAD_REQUEST, "ASG004", "파일은 필수입니다.", ErrorSeverity.INFO),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ASG005", "서버 저장소 문제로 파일 업로드에 실패했습니다.", ErrorSeverity.WARNING);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}
