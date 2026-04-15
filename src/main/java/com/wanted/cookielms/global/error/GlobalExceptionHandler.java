package com.wanted.cookielms.global.error;

import com.wanted.cookielms.global.error.exception.BusinessException;

import com.wanted.cookielms.global.error.model.ErrorLogService;
import com.wanted.cookielms.global.error.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    // =========================================================================
    // [공통 메서드] 중복 로직 제거
    // =========================================================================

    /**
     * [공통] 일반 예외 처리 (FieldError 없음)
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, Exception e, HttpServletRequest request) {
        log.warn("Exception occurred: {}", errorCode.getCode());
        // 비동기 스레드에서 HttpServletRequest에 접근할 수 없으므로, 여기서 정보 추출
        String clientIp = clientIpResolver.resolveClientIp(request);
        String userId = extractUserId();
        errorLogService.saveErrorLog(errorCode, e, clientIp, userId, request.getRequestURI(), request.getMethod());
        ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * [공통] 검증 실패 처리 (FieldError 포함)
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, Exception e, HttpServletRequest request, BindingResult bindingResult) {
        log.warn("Exception occurred: {}", errorCode.getCode());
        // 비동기 스레드에서 HttpServletRequest에 접근할 수 없으므로, 여기서 정보 추출
        String clientIp = clientIpResolver.resolveClientIp(request);
        String userId = extractUserId();
        errorLogService.saveErrorLog(errorCode, e, clientIp, userId, request.getRequestURI(), request.getMethod(), bindingResult);
        ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    private final ErrorLogService errorLogService;
    private final HttpServletRequest request;
    private final ClientIpResolver clientIpResolver;

    // =========================================================================
    // 1. 데이터 바인딩 및 검증 에러 (400 Bad Request)
    // =========================================================================

    /**
     * [C001] @Valid, @Validated 에서 검증에 실패한 경우 (JSON 요청)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, e, request, e.getBindingResult());
    }

    /**
     * [C001] @ModelAttribute 으로 바인딩 시 검증에 실패한 경우 (Form 요청)
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, e, request, e.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return buildErrorResponse(ErrorCode.INVALID_TYPE_VALUE, e, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return buildErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER, e, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return buildErrorResponse(ErrorCode.HTTP_MESSAGE_NOT_READABLE, e, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED, e, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return buildErrorResponse(ErrorCode.ACCESS_DENIED, e, request);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return buildErrorResponse(e.getErrorCode(), e, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException - 서버 내부 오류 발생!", e);
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