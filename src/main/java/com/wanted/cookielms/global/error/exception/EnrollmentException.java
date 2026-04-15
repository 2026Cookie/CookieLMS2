package com.wanted.cookielms.global.error.exception;

import com.wanted.cookielms.global.error.model.entity.ErrorSeverity;
import org.springframework.http.HttpStatus;

/**
 * 수강신청 관련 예외
 */
public class EnrollmentException extends ApplicationException {

    // 이미 수강신청이 완료된 강의
    public static EnrollmentException alreadyEnrolled(Long courseId) {
        return new EnrollmentException(HttpStatus.CONFLICT, "ENR001",
            String.format("이미 강의 ID %d에 수강신청했습니다.", courseId),
            ErrorSeverity.WARNING);
    }

    // 수강 인원 초과
    public static EnrollmentException enrollmentLimitExceeded(Long courseId) {
        return new EnrollmentException(HttpStatus.BAD_REQUEST, "ENR002",
            String.format("강의 ID %d는 수강 가능 인원을 초과했습니다.", courseId),
            ErrorSeverity.WARNING);
    }

    // 수강신청 기간이 아님
    public static EnrollmentException notEnrollmentPeriod(Long courseId) {
        return new EnrollmentException(HttpStatus.BAD_REQUEST, "ENR003",
            String.format("강의 ID %d는 현재 수강신청 기간이 아닙니다.", courseId),
            ErrorSeverity.WARNING);
    }

    public EnrollmentException(HttpStatus status, String code, String message, ErrorSeverity severity) {
        super(status, code, message, severity);
    }
}
