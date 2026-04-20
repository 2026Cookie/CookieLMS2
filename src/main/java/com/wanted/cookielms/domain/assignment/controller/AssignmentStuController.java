package com.wanted.cookielms.domain.assignment.controller;

import com.wanted.cookielms.domain.assignment.service.AssignmentStuService;
import com.wanted.cookielms.domain.auth.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/assignments")
@RequiredArgsConstructor
public class AssignmentStuController {

    private final AssignmentStuService assignmentStuService;

    @GetMapping("/{assignmentId}")
    public String showAssignmentForm(@PathVariable Long assignmentId, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("assignment", assignmentStuService.getAssignment(assignmentId));
        return "user/assignment_submit";
    }

    @PostMapping("/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam("uploadFile") MultipartFile file,
                                   @CurrentUserId Long studentId,
                                   RedirectAttributes redirectAttributes) {
        assignmentStuService.submitAssignment(assignmentId, studentId, file);
        redirectAttributes.addFlashAttribute("message", "과제가 성공적으로 제출되었습니다!");
        return "redirect:/student/assignments/" + assignmentId + "/success";
    }

    @GetMapping("/{assignmentId}/success")
    public String showSuccessPage() {
        return "user/assignment_success";
    }

}