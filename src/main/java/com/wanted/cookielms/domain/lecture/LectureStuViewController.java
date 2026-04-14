package com.wanted.cookielms.domain.lecture;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 화면(HTML)을 반환하는 컨트롤러입니다. (@RestController가 아님에 주의!)
public class LectureStuViewController {

    /**
     * 학생 전용 강의 목록 페이지로 이동합니다.
     * 브라우저 접속 주소: http://localhost:8080/student/lectures
     */
    @GetMapping("/student/lectures")
    public String studentLectureList() {
        // templates 폴더 아래에 있는 HTML 파일의 경로를 적어줍니다. (.html은 생략)
        return "role/student/lecture_list";
    }


}