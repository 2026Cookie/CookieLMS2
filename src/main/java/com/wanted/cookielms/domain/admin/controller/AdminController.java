package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLogQueryService;
import com.wanted.cookielms.domain.admin.service.ErrorLogQueryService;
import com.wanted.cookielms.domain.admin.service.BusinessServiceLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ApiPerformanceLogQueryService apiPerformanceLogQueryService;
    private final ErrorLogQueryService errorLogQueryService;
    private final BusinessServiceLogQueryService businessServiceLogQueryService;

    // 대시보드 엔드포인트는 나중에 구현
}