package com.wanted.cookielms.domain.enrollment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    @GetMapping("/student/main")
    public String main() {
        return "role/student/main";
    }

    @GetMapping("/enrollments")
    public String enrollment() {
        return "role/student/enrollment";
    }
}
