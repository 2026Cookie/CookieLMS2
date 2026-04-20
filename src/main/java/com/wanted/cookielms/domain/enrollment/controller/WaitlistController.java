package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.auth.annotation.CurrentUserId;
import com.wanted.cookielms.domain.enrollment.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    @GetMapping("/api/waitlist/my")
    @ResponseBody
    public ResponseEntity<List<Long>> getMyWaitlist(@CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(waitlistService.getMyWaitingLectureIds(userId));
    }

    @PostMapping("/api/waitlist/{lectureId}")
    @ResponseBody
    public ResponseEntity<String> registerWaitlist(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }
        int waitNumber = waitlistService.registerWaitlist(lectureId, userId);
        return ResponseEntity.ok(String.valueOf(waitNumber));
    }

    @DeleteMapping("/api/waitlist/{lectureId}")
    @ResponseBody
    public ResponseEntity<String> cancelWaitlist(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }
        waitlistService.cancelWaitlist(lectureId, userId);
        return ResponseEntity.ok("대기열 취소가 완료되었습니다.");
    }

    @GetMapping("/user/waitlist/{lectureId}")
    public String waitlistPage(@PathVariable Long lectureId, @CurrentUserId Long userId, Model model) {
        int waitNumber = waitlistService.getWaitNumber(lectureId, userId);
        model.addAttribute("waitNumber", waitNumber);
        model.addAttribute("lectureId", lectureId);

        return "user/waitlist";
    }
}
