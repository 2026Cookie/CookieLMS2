package com.wanted.cookielms.domain.assignment.controller;

import com.wanted.cookielms.domain.assignment.service.AssignmentStuService;
import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/{assignmentId}")
    public String showAssignmentForm(@PathVariable Long assignmentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("assignment", assignmentStuService.getAssignment(assignmentId));
            return "user/assignment_submit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/lectures/" + assignmentId;
        }
    }

    @PostMapping("/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam("uploadFile") MultipartFile file,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null) return "redirect:/user/login";

        try {
            AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
            Long realStudentId = authDetails.getLoginUserDTO().getUserId();

            assignmentStuService.submitAssignment(assignmentId, realStudentId, file);
            redirectAttributes.addFlashAttribute("message", "과제가 성공적으로 제출되었습니다!");
            return "redirect:/student/assignments/" + assignmentId + "/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/assignments/" + assignmentId;
        }
    }

    @GetMapping("/{assignmentId}/success")
    public String showSuccessPage() {
        return "user/assignment_success";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "최대 5MB 파일까지만 업로드 가능합니다.");
        return "redirect:" + request.getRequestURI().replace("/submit", "");
    }
}