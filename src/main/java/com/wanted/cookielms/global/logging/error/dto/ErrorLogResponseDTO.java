package com.wanted.cookielms.global.logging.error.dto;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLogResponseDTO {

    private Long id;
    private String errorCode;
    private String errorMessage;
    private String exceptionName;
    private String requestUri;
    private String httpMethod;
    private String clientIp;
    private Long userId;
    private String stackTrace;
    private String traceId;
    private ErrorSeverity severity;
    private LocalDateTime createdAt;

    /**
     * ErrorLog 엔티티 → Response DTO 변환 (모든 필드 포함)
     */
    public static ErrorLogResponseDTO from(ErrorLogEntity errorLog) {
        return ErrorLogResponseDTO.builder()
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
    public static ErrorLogResponseDTO fromList(ErrorLogEntity errorLog) {
        ErrorLogResponseDTO response = from(errorLog);
        response.stackTrace = null;
        return response;
    }
}