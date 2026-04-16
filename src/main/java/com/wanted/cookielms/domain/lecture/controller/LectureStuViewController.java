package com.wanted.cookielms.domain.lecture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LectureStuViewController {

    @GetMapping("/student/lectures")
    public String studentLectureList() {
        return "user/lecture_stu_list";
    }

    @GetMapping("/student/lectures/{lectureId}")
    public String studentLectureDetail(@PathVariable Long lectureId) {
        return "user/lecture_detail";
    }
}