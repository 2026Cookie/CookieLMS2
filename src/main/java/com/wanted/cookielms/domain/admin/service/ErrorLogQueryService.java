package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.CriticalErrorDetailDto;
import com.wanted.cookielms.domain.admin.dto.CriticalErrorListItemDto;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.logging.error.dto.ErrorLogResponseDTO;
import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogQueryService {

    private final ErrorLogRepository errorLogRepository;

    /**
     * [조회 1] 모든 에러 로그 (페이징)
     */
    public Page<ErrorLogResponseDTO> getAllErrorLogs(Pageable pageable) {
        Page<ErrorLogEntity> errorLogs = errorLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return errorLogs.map(ErrorLogResponseDTO::fromList);
    }

    /**
     * [조회 2] Trace ID로 같은 요청의 모든 에러 조회
     */
    public List<ErrorLogResponseDTO> getErrorsByTraceId(String traceId) {
        List<ErrorLogEntity> errorLogs = errorLogRepository.findByTraceIdOrderByCreatedAtDesc(traceId);
        return errorLogs.stream()
                .map(ErrorLogResponseDTO::from)
                .toList();
    }

    /**
     * [조회 3] 심각도별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponseDTO> getErrorsBySeverity(ErrorSeverity severity, Pageable pageable) {
        Page<ErrorLogEntity> errorLogs = errorLogRepository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
        return errorLogs.map(ErrorLogResponseDTO::fromList);
    }

    /**
     * [조회 4] 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponseDTO> getErrorsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLogEntity> errorLogs = errorLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponseDTO::fromList);
    }

    /**
     * [조회 5] 에러 코드별 + 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponseDTO> getErrorsByCodeAndDateRange(String errorCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLogEntity> errorLogs = errorLogRepository.findByErrorCodeAndCreatedAtBetweenOrderByCreatedAtDesc(errorCode, startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponseDTO::fromList);
    }

    /**
     * [조회 6] 심각도별 + 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponseDTO> getErrorsBySeverityAndDateRange(ErrorSeverity severity, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLogEntity> errorLogs = errorLogRepository.findBySeverityAndCreatedAtBetweenOrderByCreatedAtDesc(severity, startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponseDTO::fromList);
    }

    /**
     * [조회 7] 특정 에러 상세 조회
     */
    public ErrorLogResponseDTO getErrorDetail(Long errorId) {
        return errorLogRepository.findById(errorId)
                .map(ErrorLogResponseDTO::from)
                .orElse(null);
    }

    /**
     * [조회 8] 특정 사용자의 에러 로그 (error_logs.user_id 직접 조회)
     * Security 필터 차단(403) 케이스도 포함됨
     */
    public List<ErrorLogResponseDTO> getErrorsByUserId(Long userId) {
        return errorLogRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ErrorLogResponseDTO::fromList)
                .toList();
    }

    /**
     * [조회 8-2] 사용자별 Critical 에러 요약 (API 로그 join)
     */
    /**
     * [조회 8-2] 사용자별 Critical 에러 요약 (네이티브 쿼리로 JOIN)
     * N+1 제거: Repository의 findCriticalErrorCountGroupByUserId 활용
     */
    public List<Map<String, Object>> getCriticalErrorSummaryByUser() {
        return errorLogRepository.findCriticalErrorCountGroupByUserId();
    }

    /**
     * [조회 9] 특정 에러 코드의 로그
     */
    public List<ErrorLogResponseDTO> getErrorsByCode(String errorCode) {
        List<ErrorLogEntity> errorLogs = errorLogRepository.findByErrorCodeOrderByCreatedAtDesc(errorCode);
        return errorLogs.stream()
                .map(ErrorLogResponseDTO::fromList)
                .toList();
    }

    /**
     * [신규] Critical 에러 리스트 (페이징)
     */
    public Page<CriticalErrorListItemDto> getCriticalErrorsList(Pageable pageable) {
        return errorLogRepository.findCriticalErrorsWithApiLog(pageable);
    }

    /**
     * [신규] Critical 에러 상세 (단건)
     */
    public CriticalErrorDetailDto getCriticalErrorDetail(Long errorId) {
        return errorLogRepository.findCriticalErrorDetail(errorId);
    }
}