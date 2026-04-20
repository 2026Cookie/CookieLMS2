package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EndpointCallDetailDto {
    private String traceId;
    private String httpMethod;
    private Integer statusCode;
    private Integer executionTimeMs;
    private LocalDateTime createdAt;
}
