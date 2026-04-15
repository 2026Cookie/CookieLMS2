package com.wanted.cookielms.global.error;

import com.wanted.cookielms.global.error.model.ErrorLogService;
import com.wanted.cookielms.global.error.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;
    private final HttpServletRequest request;
    private final ClientIpResolver clientIpResolver;

    // =========================================================================
    // [공통 메서드] 에러 응답 빌드
    // =========================================================================

    /**
     * 에러 응답 생성 (검증 에러 없음)
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, Exception e, HttpServletRequest request) {
        log.warn("Exception occurred: {} - {}", errorCode.getCode(), e.getMessage());
        String clientIp = clientIpResolver.resolveClientIp(request);
        String userId = extractUserId();
        String traceId = MDC.get("traceId");
        errorLogService.saveErrorLog(errorCode, e, clientIp, userId, request.getRequestURI(), request.getMethod());
        ErrorResponse response = ErrorResponse.of(errorCode, traceId);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * ApplicationException 기반 예외 처리 (모든 커스텀 예외)
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(com.wanted.cookielms.global.error.exception.ApplicationException e, HttpServletRequest request) {
        log.warn("ApplicationException occurred: {} - {}", e.getCode(), e.getMessage());
        String clientIp = clientIpResolver.resolveClientIp(request);
        String userId = extractUserId();
        String traceId = MDC.get("traceId");

        // 비동기로 DB에 에러 로그 저장
        errorLogService.saveApplicationExceptionLog(e, clientIp, userId, traceId, request.getRequestURI(), request.getMethod());

        // 응답 생성
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus().value())
                .code(e.getCode())
                .message(e.getMessage())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(response, e.getStatus());
    }

    // =========================================================================
    // 커스텀 예외 핸들러 (ApplicationException 계열)
    // =========================================================================

    /**
     * 모든 ApplicationException 기반 예외 처리
     */
    @ExceptionHandler(com.wanted.cookielms.global.error.exception.ApplicationException.class)
    protected ResponseEntity<ErrorResponse> handleApplicationException(com.wanted.cookielms.global.error.exception.ApplicationException e) {
        return buildErrorResponse(e, request);
    }

    // =========================================================================
    // [500] 예상치 못한 모든 예외
    // =========================================================================

    /**
     * [500] 예상치 못한 모든 예외
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception occurred!", e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e, request);
    }

    /**
     * SecurityContext에서 현재 사용자를 추출합니다.
     */
    private String extractUserId() {
        String userId = "ANONYMOUS";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            userId = auth.getName();
        }
        return userId;
    }
}