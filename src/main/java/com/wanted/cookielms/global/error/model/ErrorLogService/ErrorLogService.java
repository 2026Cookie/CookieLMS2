package com.wanted.cookielms.global.error.service;

import com.wanted.cookielms.global.error.model.DTO.ErrorLogResponseDTO;
import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    /**
     * [1] 에러 로그 비동기 저장
     * Propagation.REQUIRES_NEW: 기존 비즈니스 로직의 트랜잭션 롤백과 무관하게 무조건 커밋
     * @Async: 사용자 응답 속도에 영향을 주지 않도록 별도 스레드에서 DB 저장 실행
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLogAsync(ErrorLogEntity errorLog) {
        // [1] CRITICAL 등급만 DB 저장
        if (errorLog.getSeverity() == ErrorSeverity.CRITICAL) {
            try {
                errorLogRepository.save(errorLog);
                log.error("[DB LOG] Critical Error Saved: traceId={}", errorLog.getTraceId());
            } catch (Exception e) {
                log.error("[DB LOG FAILED] traceId: {}", errorLog.getTraceId(), e);
            }
        }
        // [2] 나머지는 파일 시스템 로그로 기록 (Slf4j 활용)
        else {
            log.warn("[FILE LOG] Severity: {}, Code: {}, Message: {}, TraceId: {}",
                    errorLog.getSeverity(),
                    errorLog.getErrorCode(),
                    errorLog.getErrorMessage(),
                    errorLog.getTraceId()
            );
        }
    }

    /**
     * [2] 관리자용: 에러 로그 목록 조회 (StackTrace 제외)
     */
    @Transactional(readOnly = true)
    public Page<ErrorLogResponseDTO> getErrorLogs(Pageable pageable) {
        return errorLogRepository.findAll(pageable)
                .map(ErrorLogResponseDTO::fromList); // DTO에서 만든 fromList 메서드 사용
    }

    /**
     * [3] 관리자용: 에러 로그 상세 조회 (StackTrace 포함)
     */
    @Transactional(readOnly = true)
    public ErrorLogResponseDTO getErrorLogDetail(Long id) {
        ErrorLogEntity errorLog = errorLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 에러 로그를 찾을 수 없습니다. ID: " + id));
        return ErrorLogResponseDTO.from(errorLog); // 상세 DTO 변환
    }
}