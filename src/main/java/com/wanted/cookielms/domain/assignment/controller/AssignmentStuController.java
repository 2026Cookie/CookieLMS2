package com.wanted.cookielms.domain.assignment.controller;

import com.wanted.cookielms.domain.assignment.service.AssignmentStuService;
import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/student/assignments")
@RequiredArgsConstructor
public class AssignmentStuController {

    private final AssignmentStuService assignmentStuService;
    private final UserService userService; // 🌟 팀원분의 UserService 주입!

    // 1. 과제 폼 보여주기
    @GetMapping("/{assignmentId}")
    public String showAssignmentForm(@PathVariable Long assignmentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("assignment", assignmentStuService.getAssignment(assignmentId));
            // 🌟 수정 완료: 하연님의 원래 경로 적용
            return "user/assignment_submit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/lectures/" + assignmentId;
        }
    }

    // 2. 과제 제출 처리 (POST)
    @PostMapping("/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam("uploadFile") MultipartFile file,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        // 🛡️ 방어 1: 로그인이 안 된 상태라면 튕겨내기
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요한 서비스입니다.");
            return "redirect:/user/login";
        }

        try {
            // 시큐리티 신분증에서 아이디를 꺼내어 진짜 유저 정보(DTO)를 조회합니다.
            String loginId = principal.getName();
            LoginUserDTO loginUser = userService.findByUsername(loginId);

            // 🛡️ 방어 2: DB에서 유저 정보를 찾지 못한 경우 튕겨내기 (NullPointerException 방지)
            if (loginUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "유저 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
                return "redirect:/user/login";
            }

            // 진짜 로그인한 학생의 고유 번호(Long) 추출!
            Long realStudentId = loginUser.getUserId();

            // 10L 대신 동적으로 찾은 실제 ID를 넣어 제출 완료!
            assignmentStuService.submitAssignment(assignmentId, realStudentId, file);
            redirectAttributes.addFlashAttribute("message", "과제가 성공적으로 제출되었습니다!");
            return "redirect:/student/assignments/" + assignmentId + "/success";

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/assignments/" + assignmentId;
        } catch (Exception e) {
            // 🛡️ 방어 3: 그 외 예측 못한 에러가 나도 하얀 화면이 뜨지 않게 폼으로 돌려보냄
            redirectAttributes.addFlashAttribute("errorMessage", "과제 제출 중 서버 오류가 발생했습니다.");
            return "redirect:/student/assignments/" + assignmentId;
        }
    }

    // 3. 제출 성공 페이지
    @GetMapping("/{assignmentId}/success")
    public String showSuccessPage() {
        // 🌟 수정 완료: 하연님의 원래 경로 적용
        return "user/assignment_success";
    }

    // 4. [정책] 5MB 초과 파일 업로드 시 에러 핸들링
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "현재 서버 설정상 최대 5MB 파일까지만 업로드 가능합니다.");
        String currentUri = request.getRequestURI();
        String redirectUrl = currentUri.replace("/submit", "");
        return "redirect:" + redirectUrl;
    }
}