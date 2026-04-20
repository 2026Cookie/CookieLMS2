package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.*;
import com.wanted.cookielms.global.logging.api.repository.ApiPerformanceLogRepository;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminInsightService {

    private final ErrorLogRepository errorLogRepository;
    private final ApiPerformanceLogRepository apiPerformanceLogRepository;

    public List<InsightErrorUserDto> getCriticalErrorUsers() {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        return errorLogRepository.findCriticalErrorUsersByTime(since, 5);
    }

    public List<InsightEndpointErrorRateDto> getEndpointErrorRates() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return apiPerformanceLogRepository.findEndpointErrorRates(since, 10);
    }

    public List<InsightTrafficHeatmapDto> getTrafficHeatmap() {
        LocalDateTime since = LocalDateTime.now().minusWeeks(4);
        String[] dayNames = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        List<Object[]> rawData = apiPerformanceLogRepository.findTrafficHeatmap(since);

        return rawData.stream()
            .map(row -> {
                int dayOfWeek = ((Number) row[0]).intValue();
                int hour = ((Number) row[1]).intValue();
                long callCount = ((Number) row[2]).longValue();
                String dayName = dayNames[dayOfWeek];
                return new InsightTrafficHeatmapDto(dayOfWeek, hour, callCount, dayName);
            })
            .toList();
    }

    public InsightsAggregateDto getAllInsights() {
        return InsightsAggregateDto.builder()
            .criticalErrorUsers(getCriticalErrorUsers())
            .endpointErrorRates(getEndpointErrorRates())
            .trafficHeatmap(getTrafficHeatmap())
            .build();
    }
}
