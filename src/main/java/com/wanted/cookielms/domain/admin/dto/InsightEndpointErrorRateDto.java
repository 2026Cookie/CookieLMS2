package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InsightEndpointErrorRateDto {
    private String endpoint;
    private Long totalCalls;
    private Long errorCount;
    private Double errorRate;
}
