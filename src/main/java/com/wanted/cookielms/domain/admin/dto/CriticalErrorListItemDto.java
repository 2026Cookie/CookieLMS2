package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CriticalErrorListItemDto {
    private Long errorId;
    private String errorCode;
    private LocalDateTime createdAt;
    private String endpoint;
    private String httpMethod;
    private Integer executionTimeMs;
    private String traceId;
    private String errorMessage;
}
