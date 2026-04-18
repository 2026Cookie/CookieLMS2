package com.wanted.cookielms.domain.lecture.exception;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LectureErrorCode {

    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "LEC001", "존재하지 않는 강의입니다.", ErrorSeverity.WARNING),
    LECTURE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "LEC002", "본인의 강의만 수정할 수 있습니다.", ErrorSeverity.WARNING),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "LEC003", "PDF 형식의 파일만 업로드 가능합니다.", ErrorSeverity.INFO),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "LEC004", "파일 용량은 5MB를 초과할 수 없습니다.", ErrorSeverity.INFO),
    VIDEO_ACCESS_DENIED(HttpStatus.FORBIDDEN, "LEC005", "수강생 또는 담당 강사만 강의 영상을 재생할 수 있습니다.", ErrorSeverity.WARNING),
    MATERIAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "LEC006", "수강생 또는 담당 강사만 학습 자료를 다운로드할 수 있습니다.", ErrorSeverity.WARNING);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final ErrorSeverity severity;
}
