package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.LogService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLog;
import com.wanted.cookielms.domain.admin.service.UserBanService;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.DTO.ErrorLogResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final LogService logService;
    private final ApiPerformanceLog apiPerformanceLog;
    private final UserBanService userBanService;

    // =========================================================================
    // 에러 로그 조회 API
    // =========================================================================

    /**
     * [GET] /admin/error-logs
     * 모든 에러 로그 조회 (페이징)
     */
    @GetMapping("/error-logs")
    public Page<ErrorLogResponseDTO> getAllErrorLogs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getAllErrorLogs(pageable);
    }

    /**
     * [GET] /admin/error-logs/{id}
     * 특정 에러 상세 조회 (stackTrace 포함)
     */
    @GetMapping("/error-logs/{id}")
    public ErrorLogResponseDTO getErrorDetail(@PathVariable Long id) {
        return logService.getErrorDetail(id);
    }

    /**
     * [GET] /admin/error-logs/trace/{traceId}
     * Trace ID로 같은 요청의 모든 에러 조회
     * 예: GET /admin/error-logs/trace/a1b2c3d4-e5f6-47g8
     */
    @GetMapping("/error-logs/trace/{traceId}")
    public List<ErrorLogResponseDTO> getErrorsByTraceId(@PathVariable String traceId) {
        return logService.getErrorsByTraceId(traceId);
    }

    /**
     * [GET] /admin/error-logs/severity/{severity}
     * 심각도별 에러 조회 (페이징)
     * 예: GET /admin/error-logs/severity/CRITICAL?page=0&size=20
     */
    @GetMapping("/error-logs/severity/{severity}")
    public Page<ErrorLogResponseDTO> getErrorsBySeverity(
            @PathVariable ErrorSeverity severity,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getErrorsBySeverity(severity, pageable);
    }

    /**
     * [GET] /admin/error-logs/code/{code}
     * 에러 코드별 조회
     * 예: GET /admin/error-logs/code/C001
     */
    @GetMapping("/error-logs/code/{code}")
    public List<ErrorLogResponseDTO> getErrorsByCode(@PathVariable String code) {
        return logService.getErrorsByCode(code);
    }

    /**
     * [GET] /admin/error-logs/user/{userId}
     * 사용자별 에러 로그 조회
     * 예: GET /admin/error-logs/user/admin@test.com
     */
    @GetMapping("/error-logs/user/{userId}")
    public List<ErrorLogResponseDTO> getErrorsByUserId(@PathVariable String userId) {
        return logService.getErrorsByUserId(userId);
    }

    /**
     * [GET] /admin/error-logs/date-range
     * 기간별 에러 로그 조회 (페이징)
     * 예: GET /admin/error-logs/date-range?startDate=2026-04-01T00:00:00&endDate=2026-04-15T23:59:59&page=0
     */
    @GetMapping("/error-logs/date-range")
    public Page<ErrorLogResponseDTO> getErrorsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getErrorsByDateRange(startDate, endDate, pageable);
    }

    /**
     * [GET] /admin/error-logs/code-date-range
     * 에러 코드 + 기간별 조회 (페이징)
     * 예: GET /admin/error-logs/code-date-range?code=C001&startDate=2026-04-01T00:00:00&endDate=2026-04-15T23:59:59
     */
    @GetMapping("/error-logs/code-date-range")
    public Page<ErrorLogResponseDTO> getErrorsByCodeAndDateRange(
            @RequestParam String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getErrorsByCodeAndDateRange(code, startDate, endDate, pageable);
    }

    /**
     * [GET] /admin/error-logs/severity-date-range
     * 심각도 + 기간별 조회 (페이징)
     * 예: GET /admin/error-logs/severity-date-range?severity=CRITICAL&startDate=2026-04-01T00:00:00&endDate=2026-04-15T23:59:59
     */
    @GetMapping("/error-logs/severity-date-range")
    public Page<ErrorLogResponseDTO> getErrorsBySeverityAndDateRange(
            @RequestParam ErrorSeverity severity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getErrorsBySeverityAndDateRange(severity, startDate, endDate, pageable);
    }
}
