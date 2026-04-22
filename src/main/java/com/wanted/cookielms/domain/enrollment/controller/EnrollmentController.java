package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.auth.annotation.CurrentUserId;
import com.wanted.cookielms.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<List<Long>> getMyEnrollments(@CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(enrollmentService.getMyEnrolledLectureIds(userId));
    }

    @PostMapping("/{lectureId}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancel(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }
        enrollmentService.cancel(lectureId, userId);
        return ResponseEntity.ok("수강 취소가 완료되었습니다.");
    }

    @PostMapping("/{lectureId}")
    @ResponseBody
    public ResponseEntity<String> enroll(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }
        enrollmentService.enroll(lectureId, userId);
        return ResponseEntity.ok("수강 신청이 완료되었습니다!");
    }
}
