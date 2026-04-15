package com.wanted.cookielms.global.error.model.repository;

import com.wanted.cookielms.global.error.model.entity.ErrorSeverity;
import com.wanted.cookielms.global.error.model.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    // [조회 1] 특정 에러 코드의 로그 조회 (최근 순)
    // 예: C001, CRS001 등 특정 에러만 찾을 때
    List<ErrorLog> findByErrorCodeOrderByCreatedAtDesc(String errorCode);

    // [조회 2] 특정 사용자의 에러 로그 조회 (최근 순)
    // 예: admin@test.com 사용자가 일으킨 에러들
    List<ErrorLog> findByUserIdOrderByCreatedAtDesc(String userId);

    // [조회 3] 모든 에러 로그 조회 (페이징 + 최근 순)
    // 예: 관리자 대시보드에서 에러 목록 표시
    Page<ErrorLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // [조회 4] Trace ID로 조회 (같은 요청의 모든 에러)
    List<ErrorLog> findByTraceIdOrderByCreatedAtDesc(String traceId);

    // [조회 5] 심각도별 조회 (페이징)
    Page<ErrorLog> findBySeverityOrderByCreatedAtDesc(ErrorSeverity severity, Pageable pageable);

    // [조회 6] 기간별 조회 (페이징)
    Page<ErrorLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // [조회 7] 에러 코드 + 기간별 조회 (페이징)
    Page<ErrorLog> findByErrorCodeAndCreatedAtBetweenOrderByCreatedAtDesc(String errorCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // [조회 8] 심각도 + 기간별 조회 (페이징)
    Page<ErrorLog> findBySeverityAndCreatedAtBetweenOrderByCreatedAtDesc(ErrorSeverity severity, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

