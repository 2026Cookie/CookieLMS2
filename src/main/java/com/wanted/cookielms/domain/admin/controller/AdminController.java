package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.LogService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLog;
import com.wanted.cookielms.domain.admin.service.UserBanService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final LogService logService;
    private final ApiPerformanceLog apiPerformanceLog;
    private final UserBanService userBanService;

    public AdminController(
            AdminService adminService,
            LogService logService,
            ApiPerformanceLog apiPerformanceLog,
            UserBanService userBanService) {
        this.adminService = adminService;
        this.logService = logService;
        this.apiPerformanceLog = apiPerformanceLog;
        this.userBanService = userBanService;
    }






}
