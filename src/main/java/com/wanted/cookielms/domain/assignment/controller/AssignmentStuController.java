package com.wanted.cookielms.domain.assignment.controller;

import com.wanted.cookielms.domain.assignment.service.AssignmentStuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/assignments")
@RequiredArgsConstructor
public class AssignmentStuController {

    private final AssignmentStuService assignmentStuService;

    // 1. 과제 폼 보여주기 (과제가 없으면 강의 상세 페이지로 튕겨냄)
    @GetMapping("/{assignmentId}")
    public String showAssignmentForm(@PathVariable Long assignmentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("assignment", assignmentStuService.getAssignment(assignmentId));
            return "role/student/assignment_submit";
        } catch (IllegalArgumentException e) {
            // 과제가 없어서 에러가 터지면 낚아채서 메시지를 담고, 원래 있던 강의 상세 페이지로 되돌려 보냅니다!
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/lectures/" + assignmentId;
        }
    }

    // 2. 과제 제출 처리 (POST) -> PRG 패턴
    @PostMapping("/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam("uploadFile") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            assignmentStuService.submitAssignment(assignmentId, 10L, file); // 10L은 임시 학생 ID
            redirectAttributes.addFlashAttribute("message", "과제가 성공적으로 제출되었습니다!");
            return "redirect:/student/assignments/" + assignmentId + "/success";

        } catch (IllegalStateException | IllegalArgumentException e) {
            // 마감 기한 초과, MIME Type 위조 등의 에러를 잡아내서 화면에 띄워줍니다!
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/assignments/" + assignmentId;
        }
    }

    // 3. 제출 성공 페이지 (GET)
    @GetMapping("/{assignmentId}/success")
    public String showSuccessPage() {
        return "role/student/assignment_success";
    }

    // 4. [정책] 5MB 초과 파일 업로드 시 에러 핸들링
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "현재 서버 설정상 최대 5MB 파일까지만 업로드 가능합니다.");

        // 원래 요청했던 주소에서 /submit 을 떼고 원래 폼 화면으로 돌려보냄
        String currentUri = request.getRequestURI();
        String redirectUrl = currentUri.replace("/submit", "");
        return "redirect:" + redirectUrl;
    }
}