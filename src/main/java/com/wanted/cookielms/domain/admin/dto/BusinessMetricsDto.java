package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessMetricsDto {
    private List<SlowMethodMetric> slowMethods;
    private List<FailureMethodMetric> failureMethods;

    @Getter
    @AllArgsConstructor
    public static class SlowMethodMetric {
        private String classMethod;
        private Long callCount;
        private Double avgExecutionTimeMs;
        private Long maxExecutionTimeMs;
        private Long minExecutionTimeMs;
        private String sampleTraceId;  // 가장 느린 호출의 traceId
    }

    @Getter
    @AllArgsConstructor
    public static class FailureMethodMetric {
        private String classMethod;
        private Long failureCount;
        private Long successCount;
        private Long totalCalls;
        private String sampleTraceId;  // 가장 최근 실패의 traceId
    }
}