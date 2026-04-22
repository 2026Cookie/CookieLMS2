package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.ApiPerformanceLogDto;
import com.wanted.cookielms.domain.admin.dto.TraceDetailDto;
import com.wanted.cookielms.global.logging.api.repository.ApiPerformanceLogRepository;
import com.wanted.cookielms.global.logging.businessService.dto.BusinessServiceLogResponseDto;
import com.wanted.cookielms.global.logging.businessService.repository.BusinessServiceLogRepository;
import com.wanted.cookielms.global.logging.error.dto.ErrorLogResponseDTO;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TraceLogQueryService {

    private final ApiPerformanceLogRepository apiPerformanceLogRepository;
    private final BusinessServiceLogRepository businessServiceLogRepository;
    private final ErrorLogRepository errorLogRepository;

    public TraceDetailDto getTraceDetail(String traceId) {
        ApiPerformanceLogDto apiLog = apiPerformanceLogRepository
                .findByTraceId(traceId)
                .map(entity -> {
                    ApiPerformanceLogDto dto = new ApiPerformanceLogDto();
                    dto.setLogId(entity.getLogId());
                    dto.setEndpoint(entity.getEndpoint());
                    dto.setHttpMethod(entity.getHttpMethod());
                    dto.setExecutionTimeMs(entity.getExecutionTimeMs());
                    dto.setCreatedAt(entity.getCreatedAt());
                    dto.setUserId(entity.getUserId());
                    dto.setStatusCode(entity.getStatusCode());
                    dto.setTraceId(entity.getTraceId());
                    return dto;
                })
                .orElse(null);

        List<BusinessServiceLogResponseDto> serviceLogs = businessServiceLogRepository
                .findByTraceIdOrderByCreatedAtDesc(traceId)
                .stream()
                .map(BusinessServiceLogResponseDto::from)
                .toList();

        List<ErrorLogResponseDTO> errorLogs = errorLogRepository
                .findByTraceIdOrderByCreatedAtDesc(traceId)
                .stream()
                .map(ErrorLogResponseDTO::fromList)
                .toList();

        return new TraceDetailDto(traceId, apiLog, serviceLogs, errorLogs);
    }
}