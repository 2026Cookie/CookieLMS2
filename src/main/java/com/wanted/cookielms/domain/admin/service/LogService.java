package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.global.error.model.entity.ErrorSeverity;
import com.wanted.cookielms.global.error.model.DTO.ErrorLogResponse;
import com.wanted.cookielms.global.error.model.entity.ErrorLog;
import com.wanted.cookielms.global.error.model.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final ErrorLogRepository errorLogRepository;

    /**
     * [조회 1] 모든 에러 로그 (페이징)
     */
    public Page<ErrorLogResponse> getAllErrorLogs(Pageable pageable) {
        Page<ErrorLog> errorLogs = errorLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return errorLogs.map(ErrorLogResponse::fromList);
    }

    /**
     * [조회 2] Trace ID로 같은 요청의 모든 에러 조회
     */
    public List<ErrorLogResponse> getErrorsByTraceId(String traceId) {
        List<ErrorLog> errorLogs = errorLogRepository.findByTraceIdOrderByCreatedAtDesc(traceId);
        return errorLogs.stream()
                .map(ErrorLogResponse::from)  // 상세 정보 포함
                .toList();
    }

    /**
     * [조회 3] 심각도별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponse> getErrorsBySeverity(ErrorSeverity severity, Pageable pageable) {
        Page<ErrorLog> errorLogs = errorLogRepository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
        return errorLogs.map(ErrorLogResponse::fromList);
    }

    /**
     * [조회 4] 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponse> getErrorsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLog> errorLogs = errorLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponse::fromList);
    }

    /**
     * [조회 5] 에러 코드별 + 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponse> getErrorsByCodeAndDateRange(String errorCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLog> errorLogs = errorLogRepository.findByErrorCodeAndCreatedAtBetweenOrderByCreatedAtDesc(errorCode, startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponse::fromList);
    }

    /**
     * [조회 6] 심각도별 + 기간별 에러 로그 (페이징)
     */
    public Page<ErrorLogResponse> getErrorsBySeverityAndDateRange(ErrorSeverity severity, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<ErrorLog> errorLogs = errorLogRepository.findBySeverityAndCreatedAtBetweenOrderByCreatedAtDesc(severity, startDate, endDate, pageable);
        return errorLogs.map(ErrorLogResponse::fromList);
    }

    /**
     * [조회 7] 특정 에러 상세 조회
     */
    public ErrorLogResponse getErrorDetail(Long errorId) {
        return errorLogRepository.findById(errorId)
                .map(ErrorLogResponse::from)
                .orElse(null);
    }

    /**
     * [조회 8] 특정 사용자의 에러 로그
     */
    public List<ErrorLogResponse> getErrorsByUserId(String userId) {
        List<ErrorLog> errorLogs = errorLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return errorLogs.stream()
                .map(ErrorLogResponse::fromList)
                .toList();
    }

    /**
     * [조회 9] 특정 에러 코드의 로그
     */
    public List<ErrorLogResponse> getErrorsByCode(String errorCode) {
        List<ErrorLog> errorLogs = errorLogRepository.findByErrorCodeOrderByCreatedAtDesc(errorCode);
        return errorLogs.stream()
                .map(ErrorLogResponse::fromList)
                .toList();
    }
}
