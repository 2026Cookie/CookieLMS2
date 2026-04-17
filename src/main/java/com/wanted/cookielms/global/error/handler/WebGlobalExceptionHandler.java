package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.error.model.service.ErrorLogService;
import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
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
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        String errorMessage = "입력값 검증에 실패했습니다.";
        if (e instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException) {
            errorMessage = ((BindException) e).getBindingResult()
                    .getAllErrors().get(0).getDefaultMessage();
        }

        saveErrorLog(e, errorCode.getCode(), errorMessage, request, traceId, errorCode.getSeverity());

        return createBusinessErrorView(
                errorCode.getStatus().value(),
                errorMessage,
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [3] 지원하지 않는 HTTP 메서드 호출 (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        String errorMessage = "지원하지 않는 HTTP 메서드입니다: " + e.getMethod();

        saveErrorLog(e, errorCode.getCode(), errorMessage, request, traceId, errorCode.getSeverity());

        return createBusinessErrorView(
                errorCode.getStatus().value(),
                errorMessage,
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [4] 존재하지 않는 API 호출 (404)
     * 주의: application.yml에서 spring.mvc.throw-exception-if-no-handler-found=true 설정 필요
     * CustomErrorController와 역할 분리:
     * - WebGlobalExceptionHandler: Spring이 진입한 후 URL이 없을 때
     * - CustomErrorController: 필터 레벨이나 미등록 URL (Spring 진입 전)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.NOT_FOUND;

        saveErrorLog(e, errorCode.getCode(), errorCode.getMessage(), request, traceId, errorCode.getSeverity());

        return createBusinessErrorView(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                request.getRequestURI(),
                traceId
        );
    }

    /**
     * [5] 예상치 못한 서버 에러 (500)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnhandledException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("[UnHandled Exception] traceId: {}, message: {}", traceId, e.getMessage(), e);
        saveErrorLog(e, errorCode.getCode(), errorCode.getMessage(), request, traceId, errorCode.getSeverity());

        return createBusinessErrorView(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                request.getRequestURI(),
                traceId
        );
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
                    .requestUri(request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .clientIp(getClientIp(request))
                    .userId(getCurrentUserId())
                    .stackTrace(getStackTraceAsString(e))
                    .traceId(traceId)
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
        return UUID.randomUUID().toString();
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }
}