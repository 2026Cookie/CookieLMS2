package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // TODO: 로그인 기능 구현 후 Authentication으로 교체
    private static final Long TEMP_USER_ID = 10L;

    @PostMapping("/{lectureId}")
    public String enroll(@PathVariable Long lectureId) {
        try {
            enrollmentService.enroll(lectureId, TEMP_USER_ID);
            return "수강 신청이 완료되었습니다!";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
