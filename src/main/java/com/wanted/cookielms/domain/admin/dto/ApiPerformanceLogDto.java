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
    private String endpoint;
    private HttpMethod httpMethod;
    private Integer executionTimeMs;
    private LocalDateTime createdAt;
    private Long userId;
}