package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.LogService;
import com.wanted.cookielms.domain.admin.service.UserManagementService;
import com.wanted.cookielms.domain.admin.service.InstructorApplicationService;
import com.wanted.cookielms.domain.admin.service.UserBanService;
import com.wanted.cookielms.domain.admin.service.MetricsService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final LogService logService;
    private final UserManagementService userManagementService;
    private final InstructorApplicationService instructorApplicationService;
    private final UserBanService userBanService;
    private final MetricsService metricsService;

    public AdminController(
            AdminService adminService,
            LogService logService,
            UserManagementService userManagementService,
            InstructorApplicationService instructorApplicationService,
            UserBanService userBanService,
            MetricsService metricsService) {
        this.adminService = adminService;
        this.logService = logService;
        this.userManagementService = userManagementService;
        this.instructorApplicationService = instructorApplicationService;
        this.userBanService = userBanService;
        this.metricsService = metricsService;
    }
}
