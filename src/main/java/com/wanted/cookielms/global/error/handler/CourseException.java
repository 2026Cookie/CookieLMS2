package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import org.springframework.http.HttpStatus;

/**
 * 강의 관련 예외
 */
public class CourseException extends ApplicationException {

    // 강의를 찾을 수 없음
    public static CourseException courseNotFound(Long courseId) {
        return new CourseException(HttpStatus.NOT_FOUND, "CRS001",
            String.format("ID %d인 강의를 찾을 수 없습니다.", courseId),
            ErrorSeverity.WARNING);
    }

    // 강의 수정/삭제 권한 없음
    public static CourseException notCourseOwner() {
        return new CourseException(HttpStatus.FORBIDDEN, "CRS002",
            "이 강의를 수정/삭제할 권한이 없습니다.",
            ErrorSeverity.CRITICAL);
    }

    // 이미 폐강된 강의
    public static CourseException courseAlreadyClosed(Long courseId) {
        return new CourseException(HttpStatus.BAD_REQUEST, "CRS003",
            String.format("ID %d 강의는 이미 폐강되었습니다.", courseId),
            ErrorSeverity.WARNING);
    }

    public CourseException(HttpStatus status, String code, String message, ErrorSeverity severity) {
        super(status, code, message, severity);
    }
}
