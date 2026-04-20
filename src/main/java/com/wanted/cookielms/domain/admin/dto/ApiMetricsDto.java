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
public class ApiMetricsDto {

    private List<EndpointCallCount> topEndpoints;
    private List<DailyCallCount> dailyCalls;
    private List<HourlyTraffic> hourlyTraffic;
    private List<EndpointAvgTime> slowEndpoints;

    @Getter
    @AllArgsConstructor
    public static class EndpointCallCount {
        private String endpoint;
        private Long callCount;
    }

    @Getter
    @AllArgsConstructor
    public static class DailyCallCount {
        private String day;
        private Long callCount;
    }

    @Getter
    @AllArgsConstructor
    public static class HourlyTraffic {
        private Integer hour;
        private Long callCount;
    }

    @Getter
    @AllArgsConstructor
    public static class EndpointAvgTime {
        private String endpoint;
        private Double avgMs;
        private Long callCount;
        private String traceId;
    }
}