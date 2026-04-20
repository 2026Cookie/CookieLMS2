package com.wanted.cookielms.global.logging.error.repository;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ErrorLogRepository extends JpaRepository<ErrorLogEntity, Long> {

    List<ErrorLogEntity> findByErrorCodeOrderByCreatedAtDesc(String errorCode);

    Page<ErrorLogEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ErrorLogEntity> findByTraceIdOrderByCreatedAtDesc(String traceId);

    Page<ErrorLogEntity> findBySeverityOrderByCreatedAtDesc(ErrorSeverity severity, Pageable pageable);

    List<ErrorLogEntity> findBySeverityOrderByCreatedAtDesc(ErrorSeverity severity);

    Page<ErrorLogEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ErrorLogEntity> findByErrorCodeAndCreatedAtBetweenOrderByCreatedAtDesc(String errorCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ErrorLogEntity> findBySeverityAndCreatedAtBetweenOrderByCreatedAtDesc(ErrorSeverity severity, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.traceId IN :traceIds ORDER BY e.createdAt DESC")
    List<ErrorLogEntity> findByTraceIdIn(@Param("traceIds") List<String> traceIds);

    /**
     * 사용자별 CRITICAL 에러 건수 집계 (API 로그 join)
     * API 로그 테이블의 user_id를 기준으로 그룹핑
     */
    @Query(value = """
    SELECT a.user_id as userId, u.id as loginId, COUNT(*) as errorCount
    FROM error_logs e
    JOIN api_performance_logs a ON e.trace_id = a.trace_id
    LEFT JOIN users u ON a.user_id = u.user_Id
    WHERE e.severity = 'CRITICAL'
    GROUP BY a.user_id, u.id
    ORDER BY errorCount DESC
""", nativeQuery = true)
    List<Map<String, Object>> findCriticalErrorCountGroupByUserId();
}