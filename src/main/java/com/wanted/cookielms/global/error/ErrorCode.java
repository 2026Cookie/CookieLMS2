package com.wanted.cookielms.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==========================================
    // [C] Common: 전역 공통 에러
    // (GlobalExceptionHandler의 10대 메서드와 매핑됨)
    // ==========================================
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),           // 400 (@Valid 등)
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "파라미터 타입이 일치하지 않습니다."),   // 400 (URL 경로 변수 타입 오류)
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "C003", "필수 파라미터가 누락되었습니다."), // 400 (@RequestParam 누락)
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "C004", "JSON 파싱 중 에러가 발생했습니다."), // 400 (JSON 문법 오류)
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C005", "지원하지 않는 HTTP 메서드입니다."), // 405 (GET/POST 불일치)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C006", "서버 내부 오류가 발생했습니다."), // 500 (최후의 보루)

    // ==========================================
    // [A] Auth: 인증 및 권한 에러 (Spring Security)
    // ==========================================
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "로그인이 필요한 서비스입니다."),         // 401
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "해당 리소스에 접근할 권한이 없습니다."),    // 403 (@PreAuthorize 실패 등)
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A003", "아이디 또는 비밀번호가 일치하지 않습니다."), // 401
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "A004", "비밀번호 5회 실패로 계정이 잠겼습니다."),    // 403

    // ==========================================
    // [CRS] Course: 강의 관련 에러 (BusinessException용)
    // ==========================================
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "CRS001", "존재하지 않는 강의입니다."),         // 404
    NOT_COURSE_OWNER(HttpStatus.FORBIDDEN, "CRS002", "강의 수정/삭제 권한이 없습니다."),     // 403
    COURSE_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "CRS003", "이미 폐강된 강의입니다."),      // 400

    // ==========================================
    // [ENR] Enrollment: 수강 신청 관련 에러 (BusinessException용)
    // ==========================================
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "ENR001", "이미 수강신청이 완료된 강의입니다."),    // 409
    ENROLLMENT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "ENR002", "수강 가능 인원을 초과했습니다."), // 400
    NOT_ENROLLMENT_PERIOD(HttpStatus.BAD_REQUEST, "ENR003", "수강신청 기간이 아닙니다.");     // 400

    private final HttpStatus status;
    private final String code;
    private final String message;
}