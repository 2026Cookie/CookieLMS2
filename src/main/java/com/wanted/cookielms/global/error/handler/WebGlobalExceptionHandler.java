package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.global.aop.FileValidation.FileValidationErrorCode;
import com.wanted.cookielms.global.aop.FileValidation.FileValidationException;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;       // 🌟 추가됨
import org.springframework.http.ResponseEntity;     // 🌟 추가됨
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import org.springframework.web.servlet.resource.NoResourceFoundException;
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebGlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    /**
     * [1] 비즈니스 로직 예외 (ApplicationException)
     */
    @ExceptionHandler(ApplicationException.class)
    public ModelAndView handleApplicationException(ApplicationException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        saveErrorLog(e, e.getCode(), e.getMessage(), request, traceId, e.getSeverity());

        return createBusinessErrorView(
                e.getStatus().value(),
                e.getMessage(),
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [2] @Valid, @Validated 바인딩 에러 (400)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView handleValidationException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        GlobalErrorCode globalErrorCode = GlobalErrorCode.BAD_REQUEST;

        String errorMessage = "입력값 검증에 실패했습니다.";
        if (e instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException) {
            errorMessage = ((BindException) e).getBindingResult()
                    .getAllErrors().get(0).getDefaultMessage();
        }

        saveErrorLog(e, globalErrorCode.getCode(), errorMessage, request, traceId, globalErrorCode.getSeverity());

        return createBusinessErrorView(
                globalErrorCode.getStatus().value(),
                errorMessage,
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [3] 파일 업로드 용량 초과
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e,
                                                             HttpServletRequest request) {
        String traceId = generateTraceId();
        FileValidationErrorCode errorCode = FileValidationErrorCode.FILE_SIZE_EXCEEDED_Servlet;

        log.warn("[Multipart Exception] traceId: {}, message: {}", traceId, e.getMessage());
        saveErrorLog(e, errorCode.getCode(), errorCode.getMessage(), request, traceId, errorCode.getSeverity());

        return createBusinessErrorView(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                request.getRequestURI(),
                traceId
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        // DB 로깅 메서드(saveErrorLog)를 아예 호출하지 않습니다!
        // 콘솔에 거슬리지 않게 trace나 debug 레벨로만 살짝 남기거나 아예 생략해도 됩니다.
        log.debug("정적 리소스를 찾을 수 없습니다", e.getResourcePath());

        // 404 Not Found 상태 코드만 반환하고 조용히 요청을 종료합니다.
        return ResponseEntity.notFound().build();
    }

    /**
     * [4] 예상치 못한 서버 에러 (500)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnhandledException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        GlobalErrorCode globalErrorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        log.error("[UnHandled Exception] traceId: {}, message: {}", traceId, e.getMessage(), e);
        saveErrorLog(e, globalErrorCode.getCode(), globalErrorCode.getMessage(), request, traceId, globalErrorCode.getSeverity());

        return createBusinessErrorView(
                globalErrorCode.getStatus().value(),
                globalErrorCode.getMessage(),
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [5] 🌟 UX를 고려한 전역 Alert 예외 처리 (수정됨)
     */
    /**
     * [5] AlertException - 메시지 텍스트만 응답, DB에 에러 로그 저장
     */
    @ExceptionHandler(AlertException.class)
    public ResponseEntity<String> handleAlertException(AlertException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        saveErrorLog(e, e.getCode(), e.getMessage(), request, traceId, e.getSeverity());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return new ResponseEntity<>(e.getMessage(), headers, e.getStatus());
    }

    /**
     * [6] FileValidationException - script + redirect 방식 유지
     */
    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<String> handleFileValidationException(FileValidationException e, HttpServletRequest request) {

        String traceId = generateTraceId();
        saveErrorLog(e, e.getCode(), e.getMessage(), request, traceId, e.getSeverity());

        String safeMessage = e.getMessage() != null ? e.getMessage().replace("'", "\\'") : "오류가 발생했습니다.";
        String script;

        if (e.getRedirectUrl() == null) {
            script = String.format("<script>alert('%s'); history.back();</script>", safeMessage);
        } else {
            script = String.format("<script>alert('%s'); location.href='%s';</script>", safeMessage, e.getRedirectUrl());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

        return new ResponseEntity<>(script, headers, e.getStatus());
    }


    // =========================================================================
    // Private Helper Methods
    // =========================================================================

    private ModelAndView createBusinessErrorView(int status, String message, String requestUri, String traceId) {
        ModelAndView mav = new ModelAndView("error/business-error");
        mav.addObject("status", status);
        mav.addObject("message", message);
        mav.addObject("requestUri", requestUri);
        mav.addObject("traceId", traceId);
        return mav;
    }

    private void saveErrorLog(Exception e, String errorCodeString, String errorMessage,
                              HttpServletRequest request, String traceId, ErrorSeverity severity) {
        try {
            ErrorLogEntity errorLog = ErrorLogEntity.builder()
                    .errorCode(errorCodeString)
                    .errorMessage(errorMessage)
                    .exceptionName(e.getClass().getSimpleName())
                    .clientIp(getClientIp(request))
                    .stackTrace(getStackTraceAsString(e))
                    .traceId(traceId)
                    .userId(getCurrentUserId())
                    .severity(severity)
                    .build();

            errorLogService.saveErrorLogAsync(errorLog);
        } catch (Exception logException) {
            log.error("Error logging failed", logException);
        }
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }

    private String generateTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() &&
                    !"anonymousUser".equals(auth.getPrincipal())) {

                AuthDetails authDetails = (AuthDetails) auth.getPrincipal();
                return authDetails.getLoginUserDTO().getUserId();
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }
}