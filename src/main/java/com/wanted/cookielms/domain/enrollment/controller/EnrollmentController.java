package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{lectureId}")
    public String enroll(@PathVariable Long lectureId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인이 필요합니다.";
        }

        try {
            AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
            Long userId = authDetails.getLoginUserDTO().getUserId();
            enrollmentService.enroll(lectureId, userId);
            return "수강 신청이 완료되었습니다!";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
