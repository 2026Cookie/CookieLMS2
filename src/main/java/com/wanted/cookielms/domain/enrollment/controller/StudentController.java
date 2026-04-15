package com.wanted.cookielms.domain.enrollment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
