package com.wanted.cookielms.global.logging.error.repository;

import com.wanted.cookielms.domain.admin.dto.CriticalErrorDetailDto;
import com.wanted.cookielms.domain.admin.dto.CriticalErrorListItemDto;
import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.wanted.cookielms.domain.admin.dto.InsightErrorUserDto;
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

    /**
     * Critical 에러 리스트 + API 로그 JOIN (LEFT)
     * DTO 프로젝션으로 페이징 처리
     */
    @Query("""
    SELECT new com.wanted.cookielms.domain.admin.dto.CriticalErrorListItemDto(
        e.id, e.errorCode, e.createdAt,
        COALESCE(a.endpoint, 'N/A'), 
        COALESCE(CAST(a.httpMethod AS STRING), 'N/A'),
        a.executionTimeMs,
        e.traceId
    )
    FROM ErrorLogEntity e
    LEFT JOIN ApiPerformanceLogEntity a ON e.traceId = a.traceId
    WHERE e.severity = 'CRITICAL'
    ORDER BY e.createdAt DESC
    """)
    Page<CriticalErrorListItemDto> findCriticalErrorsWithApiLog(Pageable pageable);

    /**
     * Critical 에러 상세 (단건)
     */
    @Query("""
    SELECT new com.wanted.cookielms.domain.admin.dto.CriticalErrorDetailDto(
        e.id, e.errorCode, e.errorMessage, e.exceptionName, e.clientIp,
        e.stackTrace, e.severity, e.createdAt, e.traceId
    )
    FROM ErrorLogEntity e
    WHERE e.id = :errorId
    """)
    CriticalErrorDetailDto findCriticalErrorDetail(@Param("errorId") Long errorId);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.traceId IN :traceIds ORDER BY e.createdAt DESC")
    List<ErrorLogEntity> findByTraceIdIn(@Param("traceIds") List<String> traceIds);

    /**
     * 특정 사용자의 에러 로그 (user_id 직접 조회)
     */
    List<ErrorLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자별 CRITICAL 에러 건수 집계
     * error_logs.user_id를 기준으로 직접 그룹핑 (Security 필터 차단 요청 포함)
     * 익명 요청(user_id IS NULL)은 제외
     */
    @Query(value = """
    SELECT e.user_id as userId, u.id as loginId, COUNT(*) as errorCount
    FROM error_logs e
    LEFT JOIN users u ON e.user_id = u.user_Id
    WHERE e.severity = 'CRITICAL'
      AND e.user_id IS NOT NULL
    GROUP BY e.user_id, u.id
    ORDER BY errorCount DESC
""", nativeQuery = true)
    List<Map<String, Object>> findCriticalErrorCountGroupByUserId();

    @Query("""
        SELECT new com.wanted.cookielms.domain.admin.dto.InsightErrorUserDto(
            e.userId, u.loginId, COUNT(e), MAX(e.createdAt)
        )
        FROM ErrorLogEntity e
        LEFT JOIN User u ON e.userId = u.userId
        WHERE e.severity = 'CRITICAL'
        AND e.createdAt >= :since
        AND e.userId IS NOT NULL
        GROUP BY e.userId, u.loginId
        HAVING COUNT(e) >= :threshold
        ORDER BY COUNT(e) DESC
        """)
    List<InsightErrorUserDto> findCriticalErrorUsersByTime(
        @Param("since") LocalDateTime since,
        @Param("threshold") long threshold
    );
}