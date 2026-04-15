package com.wanted.cookielms.global.error.model.DTO;

import com.wanted.cookielms.global.error.model.entity.ErrorSeverity;
import com.wanted.cookielms.global.error.model.entity.ErrorLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLogResponse {

    private Long id;
    private String errorCode;
    private String errorMessage;
    private String exceptionName;
    private String requestUri;
    private String httpMethod;
    private String clientIp;
    private String userId;
    private String stackTrace;  // 상세 조회시만 포함
    private String traceId;
    private ErrorSeverity severity;
    private LocalDateTime createdAt;

    /**
     * ErrorLog 엔티티 → Response DTO 변환
     * (모든 필드 포함)
     */
    public static ErrorLogResponse from(ErrorLog errorLog) {
        return ErrorLogResponse.builder()
                .id(errorLog.getId())
                .errorCode(errorLog.getErrorCode())
                .errorMessage(errorLog.getErrorMessage())
                .exceptionName(errorLog.getExceptionName())
                .requestUri(errorLog.getRequestUri())
                .httpMethod(errorLog.getHttpMethod())
                .clientIp(errorLog.getClientIp())
                .userId(errorLog.getUserId())
                .stackTrace(errorLog.getStackTrace())
                .traceId(errorLog.getTraceId())
                .severity(errorLog.getSeverity())
                .createdAt(errorLog.getCreatedAt())
                .build();
    }

    /**
     * ErrorLog 엔티티 → Response DTO 변환 (List 조회용, stackTrace 제외)
     */
    public static ErrorLogResponse fromList(ErrorLog errorLog) {
        ErrorLogResponse response = from(errorLog);
        response.stackTrace = null;  // stackTrace 제외
        return response;
    }
}
