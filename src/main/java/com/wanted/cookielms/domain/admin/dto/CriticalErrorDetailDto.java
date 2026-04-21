package com.wanted.cookielms.domain.admin.dto;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CriticalErrorDetailDto {
    private Long errorId;
    private String errorCode;
    private String errorMessage;
    private String exceptionName;
    private String clientIp;
    private String stackTrace;
    private ErrorSeverity severity;
    private LocalDateTime createdAt;
    private String traceId;
}