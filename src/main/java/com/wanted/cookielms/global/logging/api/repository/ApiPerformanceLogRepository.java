package com.wanted.cookielms.global.logging.api.repository;

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
    @Query("""
            SELECT a.endpoint, COUNT(a)
            FROM ApiPerformanceLogEntity a
            WHERE a.createdAt >= :since
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

    // 엔드포인트별 평균 응답시간
    @Query("""
            SELECT a.endpoint, AVG(a.executionTimeMs)
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
}