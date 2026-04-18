package com.wanted.cookielms.domain.enrollment.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.enrollment.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<Long>> getMyWaitlist(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(List.of());
        }
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Long userId = authDetails.getLoginUserDTO().getUserId();
        return ResponseEntity.ok(waitlistService.getMyWaitingLectureIds(userId));
    }

    @PostMapping("/api/waitlist/{lectureId}")
    @ResponseBody
    public ResponseEntity<String> registerWaitlist(@PathVariable Long lectureId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }

        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Long userId = authDetails.getLoginUserDTO().getUserId();
        int waitNumber = waitlistService.registerWaitlist(lectureId, userId);
        return ResponseEntity.ok(String.valueOf(waitNumber));
    }

    @DeleteMapping("/api/waitlist/{lectureId}")
    @ResponseBody
    public ResponseEntity<String> cancelWaitlist(@PathVariable Long lectureId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok("로그인이 필요합니다.");
        }

        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Long userId = authDetails.getLoginUserDTO().getUserId();
        waitlistService.cancelWaitlist(lectureId, userId);
        return ResponseEntity.ok("대기열 취소가 완료되었습니다.");
    }

    @GetMapping("/user/waitlist/{lectureId}")
    public String waitlistPage(@PathVariable Long lectureId, Authentication authentication, Model model) {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Long userId = authDetails.getLoginUserDTO().getUserId();

        int waitNumber = waitlistService.getWaitNumber(lectureId, userId);
        model.addAttribute("waitNumber", waitNumber);
        model.addAttribute("lectureId", lectureId);

        return "user/waitlist";
    }
}