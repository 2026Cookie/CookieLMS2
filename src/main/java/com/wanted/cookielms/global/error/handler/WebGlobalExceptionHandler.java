package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.error.service.ErrorLogService;
import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class WebGlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView handleApplicationException(ApplicationException e, HttpServletRequest request) {
        String traceId = generateTraceId();

        // 1. 에러 로깅
        saveErrorLog(e, e.getCode(), e.getMessage(), request, traceId, e.getSeverity());

        // 2. HTML 렌더링 (만들어주신 UI에 데이터 주입)
        ModelAndView mav = new ModelAndView("error/business-error");

        // 💡 핵심: HTML 파일의 th:text="${...}" 변수명과 완벽하게 일치시킵니다.
        mav.addObject("status", e.getStatus().value());      // 예: 400, 404, 409 등
        mav.addObject("message", e.getMessage());            // 예: "수강 인원이 마감되었습니다."
        mav.addObject("requestUri", request.getRequestURI()); // 예: "/course/enroll"
        mav.addObject("traceId", traceId);                   // UUID

        return mav;
    }

    // =========================================================================
    // Private Helper Methods (기존과 동일)
    // =========================================================================

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
        return "ANONYMOUS"; // 보안 설정 시 연동 예정
    }
}