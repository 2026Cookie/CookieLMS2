package com.wanted.cookielms.domain.enrollment.exception;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EnrollmentErrorCode {

    ALREADY_ENROLLED(HttpStatus.CONFLICT, "ENR001", "이미 수강 신청한 강의입니다.", ErrorSeverity.INFO),
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "ENR002", "존재하지 않는 강의입니다.", ErrorSeverity.WARNING),
    ENROLLMENT_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "ENR003", "수강 정원이 초과되었습니다.", ErrorSeverity.WARNING),
    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ENR004", "수강 신청 내역을 찾을 수 없습니다.", ErrorSeverity.WARNING),
    ALREADY_WAITLISTED(HttpStatus.CONFLICT, "ENR005", "이미 대기열에 등록되어 있습니다.", ErrorSeverity.INFO),
    WAITLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ENR006", "대기열 정보를 찾을 수 없습니다.", ErrorSeverity.WARNING);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}