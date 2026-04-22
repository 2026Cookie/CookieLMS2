package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.dto.*;
import com.wanted.cookielms.domain.admin.service.AdminInsightService;
import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLogQueryService;
import com.wanted.cookielms.domain.admin.service.BusinessServiceLogQueryService;
import com.wanted.cookielms.domain.admin.service.ErrorLogQueryService;
import com.wanted.cookielms.domain.admin.service.TraceLogQueryService;
import com.wanted.cookielms.global.logging.businessService.dto.BusinessServiceLogResponseDto;
import com.wanted.cookielms.global.logging.error.dto.ErrorLogResponseDTO;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ApiPerformanceLogQueryService apiPerformanceLogQueryService;
    private final ErrorLogQueryService errorLogQueryService;
    private final BusinessServiceLogQueryService businessServiceLogQueryService;
    private final TraceLogQueryService traceLogQueryService;
    private final AdminInsightService adminInsightService;

    @ResponseBody
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getUserList() {
        return ResponseEntity.ok(adminService.getUserList());
    }

    @ResponseBody
    @GetMapping("/users/RecentBans")
    public ResponseEntity<List<AdminUserDto>> getRecentBans() {
        return ResponseEntity.ok(adminService.getBannedUserList());
    }

    @ResponseBody
    @GetMapping("/users/withdrawn")
    public ResponseEntity<List<AdminUserDto>> getWithdrawnUsers() {
        return ResponseEntity.ok(adminService.getWithdrawnUserList());
    }

    @ResponseBody
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/logs/traffic")
    public ResponseEntity<ApiMetricsDto> getTrafficMetrics() {
        return ResponseEntity.ok(apiPerformanceLogQueryService.getMetrics());
    }

    @ResponseBody
    @GetMapping("/logs/performance")
    public ResponseEntity<BusinessMetricsDto> getPerformanceMetrics() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<BusinessMetricsDto.SlowMethodMetric> slow = businessServiceLogQueryService.getTopSlowMethods(10, since);
        List<BusinessMetricsDto.FailureMethodMetric> failures = businessServiceLogQueryService.getTopFailureMethods(10, since);
        return ResponseEntity.ok(BusinessMetricsDto.builder()
                .slowMethods(slow)
                .failureMethods(failures)
                .build());
    }

    @ResponseBody
    @GetMapping("/logs/errors")
    public ResponseEntity<List<Map<String, Object>>> getCriticalErrors() {
        return ResponseEntity.ok(errorLogQueryService.getCriticalErrorSummaryByUser());
    }

    @ResponseBody
    @GetMapping("/logs/trace/{traceId}")
    public ResponseEntity<TraceDetailDto> getTraceDetail(@PathVariable String traceId) {
        return ResponseEntity.ok(traceLogQueryService.getTraceDetail(traceId));
    }

    @ResponseBody
    @GetMapping("/users/{userId}/errors")
    public ResponseEntity<List<ErrorLogResponseDTO>> getUserErrors(@PathVariable Long userId) {
        return ResponseEntity.ok(errorLogQueryService.getErrorsByUserId(userId));
    }

    @ResponseBody
    @GetMapping("/users/{userId}/service-failures")
    public ResponseEntity<List<BusinessServiceLogResponseDto>> getUserServiceFailures(@PathVariable Long userId) {
        return ResponseEntity.ok(businessServiceLogQueryService.getFailuresByUserId(userId));
    }

    @ResponseBody
    @GetMapping("/logs/errors/critical")
    public ResponseEntity<Page<CriticalErrorListItemDto>> getCriticalErrorsList(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(errorLogQueryService.getCriticalErrorsList(pageable));
    }

    @ResponseBody
    @GetMapping("/logs/errors/{errorId}")
    public ResponseEntity<CriticalErrorDetailDto> getCriticalErrorDetail(@PathVariable Long errorId) {
        CriticalErrorDetailDto detail = errorLogQueryService.getCriticalErrorDetail(errorId);
        return detail != null
                ? ResponseEntity.ok(detail)
                : ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/logs/errors/anonymous")
    public ResponseEntity<List<ErrorLogResponseDTO>> getAnonymousCriticalErrors() {
        return ResponseEntity.ok(errorLogQueryService.getAnonymousCriticalErrors());
    }

    @ResponseBody
    @GetMapping("/insights")
    public ResponseEntity<InsightsAggregateDto> getInsights() {
        return ResponseEntity.ok(adminInsightService.getAllInsights());
    }

    @ResponseBody
    @GetMapping("/logs/endpoint/**")
    public ResponseEntity<List<EndpointCallDetailDto>> getEndpointCallDetails(jakarta.servlet.http.HttpServletRequest request) {
        String endpoint = request.getRequestURI().substring("/admin/logs/endpoint".length());
        if (endpoint.isEmpty()) endpoint = "/";
        return ResponseEntity.ok(apiPerformanceLogQueryService.getEndpointCallDetails(endpoint));
    }

    @ResponseBody
    @GetMapping("/logs/method/{classMethod}")
    public ResponseEntity<List<Map<String, Object>>> getMethodCallDetails(@PathVariable String classMethod) {
        return ResponseEntity.ok(businessServiceLogQueryService.getMethodCallDetails(classMethod));
    }

}
