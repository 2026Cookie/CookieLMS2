package com.wanted.cookielms.global.logging.error.service;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.logging.error.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    /**
     * 에러 로그 비동기 저장
     * Propagation.REQUIRES_NEW: 기존 비즈니스 로직의 트랜잭션 롤백과 무관하게 무조건 커밋
     *
     * @Async: 사용자 응답 속도에 영향을 주지 않도록 별도 스레드에서 DB 저장 실행
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLogAsync(ErrorLogEntity errorLog) {
        // createdAt 설정 (@CreatedDate 자동 설정용)
        if (errorLog.getCreatedAt() == null) {
            try {
                var field = errorLog.getClass().getDeclaredField("createdAt");
                field.setAccessible(true);
                field.set(errorLog, LocalDateTime.now());
            } catch (Exception e) {
                log.debug("Failed to set createdAt", e);
            }
        }

        // CRITICAL 등급만 DB 저장
        if (errorLog.getSeverity() == ErrorSeverity.CRITICAL) {
            try {
                errorLogRepository.save(errorLog);
                log.error("[DB LOG] Critical Error Saved: traceId={}", errorLog.getTraceId());
            } catch (Exception e) {
                log.error("[DB LOG FAILED] traceId: {}", errorLog.getTraceId(), e);
            }
        }
        // 나머지는 파일 시스템 로그로 기록 (Slf4j 활용)
        else {
            log.warn("[FILE LOG] Severity: {}, Code: {}, Message: {}, TraceId: {}",
                    errorLog.getSeverity(),
                    errorLog.getErrorCode(),
                    errorLog.getErrorMessage(),
                    errorLog.getTraceId()
            );
        }
    }
}