package com.wanted.cookielms.global.logging.businessService.repository;

import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface BusinessServiceLogRepository extends JpaRepository<BusinessServiceLogEntity, Long> {

    // 오류 발생 기능 top N
    @Query(value = """
        SELECT 
            class_method as classMethod,
            SUM(CASE WHEN is_success = false THEN 1 ELSE 0 END) as failureCount,
            SUM(CASE WHEN is_success = true THEN 1 ELSE 0 END) as successCount,
            COUNT(*) as totalCalls
        FROM business_service_logs
        WHERE is_success = false AND created_at >= :startTime
        GROUP BY class_method
        ORDER BY failureCount DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Map<String, Object>> findTopFailureMethods(
            @Param("startTime") LocalDateTime startTime,
            @Param("limit") int limit
    );

    // 느린 기능 top N
    @Query(value = """
        SELECT 
            class_method as classMethod,
            COUNT(*) as callCount,
            AVG(execution_time_ms) as avgTime,
            MAX(execution_time_ms) as maxTime,
            MIN(execution_time_ms) as minTime
        FROM business_service_logs
        WHERE created_at >= :startTime
        GROUP BY class_method
        ORDER BY avgTime DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Map<String, Object>> findTopSlowMethods(
            @Param("startTime") LocalDateTime startTime,
            @Param("limit") int limit
    );

    // 특정 메서드의 실패 기록 조회
    List<BusinessServiceLogEntity> findByClassMethodAndIsSuccessFalse(String classMethod);
}