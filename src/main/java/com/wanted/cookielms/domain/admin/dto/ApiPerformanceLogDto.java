package com.wanted.cookielms.domain.admin.dto;

import lombok.*;
import com.wanted.cookielms.domain.admin.enums.HttpMethod;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApiPerformanceLogDto {
    private Long logId;
    private Long userId;
    private String endpoint;
    private HttpMethod httpMethod;
    private Integer statusCode;
    private Integer executionTimeMs;
    private String clientIp;
    private LocalDateTime createdAt;
}
