package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/my")
    public ResponseEntity<List<Long>> getMyEnrollments(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(List.of());
        }
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Long userId = authDetails.getLoginUserDTO().getUserId();
        return ResponseEntity.ok(enrollmentService.getMyEnrolledLectureIds(userId));
    }

    @DeleteMapping("/{lectureId}")
    public String cancel(@PathVariable Long lectureId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인이 필요합니다.";
        }
        try {
            AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
            Long userId = authDetails.getLoginUserDTO().getUserId();
            enrollmentService.cancel(lectureId, userId);
            return "수강 취소가 완료되었습니다.";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return e.getMessage();
        }
    }

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
