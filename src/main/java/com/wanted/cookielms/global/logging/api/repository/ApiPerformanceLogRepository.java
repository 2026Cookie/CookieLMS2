package com.wanted.cookielms.global.logging.api.repository;

import com.wanted.cookielms.domain.admin.dto.InsightEndpointErrorRateDto;
import com.wanted.cookielms.global.logging.api.entity.ApiPerformanceLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApiPerformanceLogRepository extends JpaRepository<ApiPerformanceLogEntity, Long> {

    // 호출 많은 API Top N
// 호출 많은 API Top N (페이지 엔드포인트만 = GET + 블랙리스트 제외)
    @Query("""
    SELECT a.endpoint, COUNT(a)
    FROM ApiPerformanceLogEntity a
    WHERE a.createdAt >= :since
    AND a.httpMethod = 'GET'
    AND (a.endpoint NOT LIKE '/admin/logs%'
        AND a.endpoint NOT LIKE '/admin/users%'
        AND a.endpoint NOT LIKE '/admin/insights%'
        AND a.endpoint NOT LIKE '/user/waitlist%'
        AND a.endpoint NOT LIKE '/uploads/%'
        AND a.endpoint NOT LIKE '/css/%'
        AND a.endpoint NOT LIKE '/js/%'
        AND a.endpoint NOT LIKE '/favicon%'
        AND a.endpoint NOT LIKE '/error'
        AND a.endpoint NOT LIKE '/actuator%')
    GROUP BY a.endpoint
    ORDER BY COUNT(a) DESC
    """)
    List<Object[]> findTopEndpoints(@Param("since") LocalDateTime since, Pageable pageable);

    // 일별 호출 수 (최근 14일)
    @Query("""
            SELECT FUNCTION('DATE', a.createdAt), COUNT(a)
            FROM ApiPerformanceLogEntity a
            WHERE a.createdAt >= :since
            GROUP BY FUNCTION('DATE', a.createdAt)
            ORDER BY FUNCTION('DATE', a.createdAt) ASC
            """)
    List<Object[]> findDailyCallCounts(@Param("since") LocalDateTime since);

    // 오늘 시간별 트래픽
    @Query("""
            SELECT FUNCTION('HOUR', a.createdAt), COUNT(a)
            FROM ApiPerformanceLogEntity a
            WHERE a.createdAt >= :startOfDay
            GROUP BY FUNCTION('HOUR', a.createdAt)
            ORDER BY FUNCTION('HOUR', a.createdAt) ASC
            """)
    List<Object[]> findHourlyTrafficToday(@Param("startOfDay") LocalDateTime startOfDay);

    // 엔드포인트별 평균 응답시간 + 호출 수
    @Query("""
    SELECT a.endpoint, AVG(a.executionTimeMs), COUNT(a), MAX(a.id)
    FROM ApiPerformanceLogEntity a
    WHERE a.createdAt >= :since
    GROUP BY a.endpoint
    ORDER BY AVG(a.executionTimeMs) DESC
    """)
    List<Object[]> findAvgResponseTimeByEndpoint(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * traceId로 API 로그 단일 조회
     */
    Optional<ApiPerformanceLogEntity> findByTraceId(String traceId);

    /**
     * 특정 userId의 모든 traceId 조회
     */
    @Query("SELECT a.traceId FROM ApiPerformanceLogEntity a WHERE a.userId = :userId")
    List<String> findTraceIdsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new com.wanted.cookielms.domain.admin.dto.InsightEndpointErrorRateDto(
            a.endpoint,
            COUNT(a),
            COUNT(DISTINCT CASE WHEN e.traceId IS NOT NULL THEN e.traceId END),
            CAST(COUNT(DISTINCT CASE WHEN e.traceId IS NOT NULL THEN e.traceId END) AS DOUBLE)
                / COUNT(a) * 100
        )
        FROM ApiPerformanceLogEntity a
        LEFT JOIN ErrorLogEntity e ON a.traceId = e.traceId
        WHERE a.createdAt >= :since
        AND a.httpMethod = 'GET'
        AND a.endpoint NOT LIKE '/admin/logs%'
        AND a.endpoint NOT LIKE '/admin/users/%'
        AND a.endpoint NOT LIKE '/uploads/%'
        AND a.endpoint NOT LIKE '/css/%'
        AND a.endpoint NOT LIKE '/js/%'
        AND a.endpoint NOT LIKE '/favicon%'
        AND a.endpoint NOT LIKE '/error'
        AND a.endpoint NOT LIKE '/actuator%'
        GROUP BY a.endpoint
        HAVING COUNT(a) >= :minCalls
        ORDER BY (COUNT(DISTINCT CASE WHEN e.traceId IS NOT NULL THEN e.traceId END) / COUNT(a)) DESC
        """)
    List<InsightEndpointErrorRateDto> findEndpointErrorRates(
        @Param("since") LocalDateTime since,
        @Param("minCalls") long minCalls
    );

    @Query(value = """
        SELECT
            DAYOFWEEK(a.created_at) as dayOfWeek,
            HOUR(a.created_at) as hour,
            COUNT(*) as callCount
        FROM api_performance_logs a
        WHERE a.created_at >= :since
        AND a.http_method = 'GET'
        AND a.endpoint NOT LIKE '/admin/logs%'
        AND a.endpoint NOT LIKE '/admin/users/%'
        AND a.endpoint NOT LIKE '/uploads/%'
        AND a.endpoint NOT LIKE '/css/%'
        AND a.endpoint NOT LIKE '/js/%'
        AND a.endpoint NOT LIKE '/favicon%'
        AND a.endpoint NOT LIKE '/error'
        AND a.endpoint NOT LIKE '/actuator%'
        GROUP BY DAYOFWEEK(a.created_at), HOUR(a.created_at)
        ORDER BY dayOfWeek, hour
        """, nativeQuery = true)
    List<Object[]> findTrafficHeatmap(@Param("since") LocalDateTime since);

    List<ApiPerformanceLogEntity> findByEndpointOrderByCreatedAtDesc(String endpoint, Pageable pageable);

}