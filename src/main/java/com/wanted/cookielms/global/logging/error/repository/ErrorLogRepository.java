package com.wanted.cookielms.global.logging.error.repository;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ErrorLogRepository extends JpaRepository<ErrorLogEntity, Long> {

    List<ErrorLogEntity> findByErrorCodeOrderByCreatedAtDesc(String errorCode);

    List<ErrorLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<ErrorLogEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ErrorLogEntity> findByTraceIdOrderByCreatedAtDesc(String traceId);

    Page<ErrorLogEntity> findBySeverityOrderByCreatedAtDesc(ErrorSeverity severity, Pageable pageable);

    Page<ErrorLogEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ErrorLogEntity> findByErrorCodeAndCreatedAtBetweenOrderByCreatedAtDesc(String errorCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ErrorLogEntity> findBySeverityAndCreatedAtBetweenOrderByCreatedAtDesc(ErrorSeverity severity, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query(value = """
    SELECT e.user_id as userId, u.id as loginId, COUNT(*) as errorCount
    FROM error_logs e
    LEFT JOIN users u ON e.user_id = u.user_Id
    WHERE e.severity = 'CRITICAL'
    GROUP BY e.user_id, u.id
    ORDER BY errorCount DESC
""", nativeQuery = true)
    List<Map<String, Object>> findCriticalErrorCountGroupByUserId();
}