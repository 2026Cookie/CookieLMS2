package com.wanted.cookielms.domain.admin.controller;

import com.wanted.cookielms.domain.admin.dto.AdminUserDto;
import com.wanted.cookielms.domain.admin.service.AdminService;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLogQueryService;
import com.wanted.cookielms.domain.admin.service.ErrorLogQueryService;
import com.wanted.cookielms.domain.admin.service.BusinessServiceLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ApiPerformanceLogQueryService apiPerformanceLogQueryService;
    private final ErrorLogQueryService errorLogQueryService;
    private final BusinessServiceLogQueryService businessServiceLogQueryService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getUserList() {
        return ResponseEntity.ok(adminService.getUserList());
    }

    @GetMapping("/users/RecentBans")
    public ResponseEntity<List<AdminUserDto>> getRecentBans() {
        return ResponseEntity.ok(adminService.getBannedUserList());
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }
}
