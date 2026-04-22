package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.BusinessMetricsDto;
import com.wanted.cookielms.global.logging.api.repository.ApiPerformanceLogRepository;
import com.wanted.cookielms.global.logging.businessService.dto.BusinessServiceLogResponseDto;
import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.businessService.repository.BusinessServiceLogRepository;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BusinessServiceLogQueryService {

    private final BusinessServiceLogRepository businessServiceLogRepository;
    private final ErrorLogRepository errorLogRepository;
    private final ApiPerformanceLogRepository apiPerformanceLogRepository;

    /**
     * 특정 사용자의 서비스 실패 로그 (API 로그 join으로 traceId 획득)
     */
    public List<BusinessServiceLogResponseDto> getFailuresByUserId(Long userId) {
        List<String> traceIds = apiPerformanceLogRepository.findTraceIdsByUserId(userId);

        if (traceIds.isEmpty()) {
            return List.of();
        }

        return businessServiceLogRepository
                .findByTraceIdInAndIsSuccessFalseOrderByCreatedAtDesc(traceIds)
                .stream()
                .map(BusinessServiceLogResponseDto::from)
                .toList();
    }

    /**
     * 📊 오류 발생이 많은 기능 top N
     */
    public List<BusinessMetricsDto.SlowMethodMetric> getTopSlowMethods(int limit, LocalDateTime startTime) {
        return businessServiceLogRepository.findTopSlowMethods(startTime, limit)
                .stream()
                .map(row -> new BusinessMetricsDto.SlowMethodMetric(
                        (String) row.get("classMethod"),
                        ((Number) row.get("callCount")).longValue(),
                        ((Number) row.get("avgTime")).doubleValue(),
                        ((Number) row.get("maxTime")).longValue(),
                        ((Number) row.get("minTime")).longValue(),
                        (String) row.get("sampleTraceId")
                ))
                .toList();
    }

    public List<BusinessMetricsDto.FailureMethodMetric> getTopFailureMethods(int limit, LocalDateTime startTime) {
        return businessServiceLogRepository.findTopFailureMethods(startTime, limit)
                .stream()
                .map(row -> new BusinessMetricsDto.FailureMethodMetric(
                        (String) row.get("classMethod"),
                        ((Number) row.get("failureCount")).longValue(),
                        ((Number) row.get("successCount")).longValue(),
                        ((Number) row.get("totalCalls")).longValue(),
                        (String) row.get("sampleTraceId")
                ))
                .toList();
    }

    public List<Map<String, Object>> getMethodCallDetails(String classMethod) {
        List<BusinessServiceLogEntity> logs = businessServiceLogRepository
                .findByClassMethodOrderByCreatedAtDesc(classMethod);

        return logs.stream()
                .limit(20)
                .map(log -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("traceId", log.getTraceId());
                    detail.put("classMethod", log.getClassMethod());
                    detail.put("executionTimeMs", log.getExecutionTimeMs());
                    detail.put("isSuccess", log.getIsSuccess());
                    detail.put("createdAt", log.getCreatedAt());
                    return detail;
                })
                .collect(Collectors.toList());
    }

    /**
     * 📊 특정 메서드의 오류 상세 (ErrorLog와 연결, API 로그에서 userId join)
     */
    public List<Map<String, Object>> getErrorDetailsByMethod(String classMethod) {
        List<BusinessServiceLogEntity> failedLogs =
                businessServiceLogRepository.findByClassMethodAndIsSuccessFalse(classMethod);

        return failedLogs.stream()
                .map(log -> {
                    Long userId = apiPerformanceLogRepository.findByTraceId(log.getTraceId())
                            .map(api -> api.getUserId())
                            .orElse(null);

                    Map<String, Object> detail = new HashMap<>();
                    detail.put("methodName", log.getClassMethod());
                    detail.put("executionTimeMs", log.getExecutionTimeMs());
                    detail.put("userId", userId);
                    detail.put("traceId", log.getTraceId());
                    detail.put("createdAt", log.getCreatedAt());
                    return detail;
                })
                .collect(Collectors.toList());
    }
}
