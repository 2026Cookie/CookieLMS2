package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.ApiMetricsDto;
import com.wanted.cookielms.global.logging.api.entity.ApiPerformanceLogEntity;
import com.wanted.cookielms.global.logging.api.repository.ApiPerformanceLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanted.cookielms.domain.admin.dto.EndpointCallDetailDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiPerformanceLogQueryService {

    private final ApiPerformanceLogRepository apiPerformanceLogRepository;

    @Transactional(readOnly = true)
    public ApiMetricsDto getMetrics() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        List<ApiMetricsDto.EndpointCallCount> topEndpoints = apiPerformanceLogRepository
                .findTopEndpoints(sevenDaysAgo, PageRequest.of(0, 10))
                .stream()
                .map(row -> new ApiMetricsDto.EndpointCallCount((String) row[0], (Long) row[1]))
                .toList();

        List<ApiMetricsDto.DailyCallCount> dailyCalls = apiPerformanceLogRepository
                .findDailyCallCounts(fourteenDaysAgo)
                .stream()
                .map(row -> new ApiMetricsDto.DailyCallCount(row[0].toString(), (Long) row[1]))
                .toList();

        List<ApiMetricsDto.HourlyTraffic> hourlyTraffic = apiPerformanceLogRepository
                .findHourlyTrafficToday(startOfToday)
                .stream()
                .map(row -> new ApiMetricsDto.HourlyTraffic((Integer) row[0], (Long) row[1]))
                .toList();

List<ApiMetricsDto.EndpointAvgTime> slowEndpoints = apiPerformanceLogRepository
                .findAvgResponseTimeByEndpoint(sevenDaysAgo, PageRequest.of(0, 10))
                .stream()
                .map(row -> {
                    Long id = ((Number) row[3]).longValue();
                    String traceId = apiPerformanceLogRepository.findById(id)
                            .map(ApiPerformanceLogEntity::getTraceId)
                            .orElse(null);
                    return new ApiMetricsDto.EndpointAvgTime(
                            (String) row[0],
                            (Double) row[1],
                            ((Number) row[2]).longValue(),
                            traceId
                    );
                })
                .toList();

        return ApiMetricsDto.builder()
                .topEndpoints(topEndpoints)
                .dailyCalls(dailyCalls)
                .hourlyTraffic(hourlyTraffic)
                .slowEndpoints(slowEndpoints)
                .build();
    }

    @Transactional(readOnly = true)
    public List<EndpointCallDetailDto> getEndpointCallDetails(String endpoint) {
        return apiPerformanceLogRepository
                .findByEndpointOrderByCreatedAtDesc(endpoint, PageRequest.of(0, 20))
                .stream()
                .map(log -> new EndpointCallDetailDto(
                        log.getTraceId(),
                        log.getHttpMethod().name(),
                        log.getStatusCode(),
                        log.getExecutionTimeMs(),
                        log.getCreatedAt()
                ))
                .toList();
    }
}