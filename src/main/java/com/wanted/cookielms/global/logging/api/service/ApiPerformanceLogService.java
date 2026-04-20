package com.wanted.cookielms.global.logging.api.service;

import com.wanted.cookielms.domain.admin.dto.ApiPerformanceLogDto;
import com.wanted.cookielms.global.logging.api.entity.ApiPerformanceLogEntity;
import com.wanted.cookielms.global.logging.api.repository.ApiPerformanceLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiPerformanceLogService {

    private final ApiPerformanceLogRepository apiPerformanceLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAsync(ApiPerformanceLogDto dto) {
        try {
            ApiPerformanceLogEntity entity = new ApiPerformanceLogEntity();
            entity.setEndpoint(dto.getEndpoint());
            entity.setHttpMethod(dto.getHttpMethod());
            entity.setExecutionTimeMs(dto.getExecutionTimeMs());
            entity.setCreatedAt(dto.getCreatedAt());
            entity.setUserId(dto.getUserId());
            entity.setStatusCode(dto.getStatusCode());

            apiPerformanceLogRepository.save(entity);
        } catch (Exception e) {
            log.error("[API LOG FAILED] endpoint: {}", dto.getEndpoint(), e);
        }
    }
}