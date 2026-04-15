package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.LogService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLog;
import com.wanted.cookielms.domain.admin.service.UserBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final LogService logService;
    private final ApiPerformanceLog apiPerformanceLog;
    private final UserBanService userBanService;

}
