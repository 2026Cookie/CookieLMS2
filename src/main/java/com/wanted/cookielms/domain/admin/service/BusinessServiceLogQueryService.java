package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.businessService.repository.BusinessServiceLogRepository;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessServiceLogQueryService {

    private final BusinessServiceLogRepository businessServiceLogRepository;
    private final ErrorLogRepository errorLogRepository;

    /**
     * 📊 오류 발생이 많은 기능 top N
     */
    public List<Map<String, Object>> getTopFailureMethods(int limit, LocalDateTime startTime) {
        return businessServiceLogRepository.findTopFailureMethods(startTime, limit);
    }

    /**
     * 📊 느린 기능 top N
     */
    public List<Map<String, Object>> getTopSlowMethods(int limit, LocalDateTime startTime) {
        return businessServiceLogRepository.findTopSlowMethods(startTime, limit);
    }

    /**
     * 📊 특정 메서드의 오류 상세 (ErrorLog와 연결)
     */
    public List<Map<String, Object>> getErrorDetailsByMethod(String classMethod) {
        List<BusinessServiceLogEntity> failedLogs =
                businessServiceLogRepository.findByClassMethodAndIsSuccessFalse(classMethod);

        return failedLogs.stream()
                .map(log -> {
                    Map<String, Object> detail = Map.of(
                            "methodName", log.getClassMethod(),
                            "executionTimeMs", log.getExecutionTimeMs(),
                            "userId", log.getUserId(),
                            "traceId", log.getTraceId(),
                            "createdAt", log.getCreatedAt()
                    );
                    return detail;
                })
                .collect(Collectors.toList());
    }
}