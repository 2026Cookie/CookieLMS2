package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.TraceDetailDto;
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

    private final BusinessServiceLogRepository businessServiceLogRepository;
    private final ErrorLogRepository errorLogRepository;

    public TraceDetailDto getTraceDetail(String traceId) {
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

        return new TraceDetailDto(traceId, serviceLogs, errorLogs);
    }
}