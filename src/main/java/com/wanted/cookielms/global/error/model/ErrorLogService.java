package com.wanted.cookielms.global.error.model;

import com.wanted.cookielms.global.error.ErrorCode;
import com.wanted.cookielms.global.error.exception.ApplicationException;
import com.wanted.cookielms.global.error.model.entity.ErrorLog;
import com.wanted.cookielms.global.error.model.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Value;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    @Value("${app.error-log.stack-trace-max-length:1000}")
    private int stackTraceMaxLength;
    /**
     * 일반 예외를 DB에 저장합니다.
     */
    @Async
    @Transactional
    public void saveErrorLog(ErrorCode errorCode, Exception e, String clientIp, String userId, String requestUri, String httpMethod) {
        try {
            // 1. MDC에서 traceId 추출
            String traceId = MDC.get("traceId");

            // 2. 스택 트레이스 추출 및 포맷팅
            String stackTrace = formatStackTrace(e);
            String combined = buildStackTraceWithErrors("", stackTrace, stackTraceMaxLength);

            // 3. 엔티티 생성 및 저장
            ErrorLog errorLog = ErrorLog.builder()
                    .errorCode(errorCode.getCode())
                    .errorMessage(e.getMessage() != null ? e.getMessage() : errorCode.getMessage())
                    .exceptionName(e.getClass().getSimpleName())
                    .requestUri(requestUri)
                    .httpMethod(httpMethod)
                    .clientIp(clientIp)
                    .userId(userId)
                    .stackTrace(combined)
                    .traceId(traceId)
                    .severity(errorCode.getSeverity())
                    .build();

            errorLogRepository.save(errorLog);

        } catch (Exception logException) {
            log.error("에러 로그 저장 중 실패 (원래 에러 응답에는 영향 없음)", logException);
        }
    }

    /**
     * 필드 검증 실패 예외를 BindingResult 정보와 함께 DB에 저장합니다.
     */
    @Async
    @Transactional
    public void saveErrorLog(ErrorCode errorCode, Exception e, String clientIp, String userId, String requestUri, String httpMethod, BindingResult bindingResult) {
        try {
            // 1. MDC에서 traceId 추출
            String traceId = MDC.get("traceId");

            // 2. 필드 에러와 스택 트레이스 추출
            String fieldErrors = formatFieldErrors(bindingResult);
            String stackTrace = formatStackTrace(e);
            String combined = buildStackTraceWithErrors(fieldErrors, stackTrace, stackTraceMaxLength);

            // 3. 엔티티 생성 및 저장
            ErrorLog errorLog = ErrorLog.builder()
                    .errorCode(errorCode.getCode())
                    .errorMessage(e.getMessage() != null ? e.getMessage() : errorCode.getMessage())
                    .exceptionName(e.getClass().getSimpleName())
                    .requestUri(requestUri)
                    .httpMethod(httpMethod)
                    .clientIp(clientIp)
                    .userId(userId)
                    .stackTrace(combined)
                    .traceId(traceId)
                    .severity(errorCode.getSeverity())
                    .build();

            errorLogRepository.save(errorLog);

        } catch (Exception logException) {
            log.error("에러 로그 저장 중 실패 (원래 에러 응답에는 영향 없음)", logException);
        }
    }

    // =========================================================================
    // [private 메서드] 책임 분리
    // =========================================================================

    /**
     * BindingResult의 필드 검증 정보를 포맷팅합니다.
     */
    private String formatFieldErrors(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        if (bindingResult.hasErrors()) {
            sb.append("=== Field Errors ===\n");
            bindingResult.getFieldErrors().forEach(error ->
                sb.append("- Field: ").append(error.getField())
                  .append(", Value: ").append(error.getRejectedValue())
                  .append(", Reason: ").append(error.getDefaultMessage())
                  .append("\n")
            );
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Exception의 스택 트레이스를 추출합니다.
     */
    private String formatStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * 필드 에러 정보와 스택 트레이스를 결합하고 길이를 제한합니다.
     */
    private String buildStackTraceWithErrors(String fieldErrors, String stackTrace, int maxLength) {
        String combined = fieldErrors + stackTrace;
        if (combined.length() > maxLength) {
            return combined.substring(0, maxLength);
        }
        return combined;
    }

    /**
     * ApplicationException (커스텀 예외)을 DB에 저장합니다.
     */
    @Async
    @Transactional
    public void saveApplicationExceptionLog(ApplicationException e, String clientIp, String userId, String traceId, String requestUri, String httpMethod) {
        try {
            // 스택 트레이스 추출
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            if (stackTrace.length() > stackTraceMaxLength) {
                stackTrace = stackTrace.substring(0, stackTraceMaxLength);
            }

            // 엔티티 생성 및 저장
            ErrorLog errorLog = ErrorLog.builder()
                    .errorCode(e.getCode())
                    .errorMessage(e.getMessage())
                    .exceptionName(e.getClass().getSimpleName())
                    .requestUri(requestUri)
                    .httpMethod(httpMethod)
                    .clientIp(clientIp)
                    .userId(userId)
                    .stackTrace(stackTrace)
                    .traceId(traceId)
                    .severity(e.getSeverity())
                    .build();

            errorLogRepository.save(errorLog);

        } catch (Exception logException) {
            log.error("에러 로그 저장 중 실패 (원래 에러 응답에는 영향 없음)", logException);
        }
    }
}