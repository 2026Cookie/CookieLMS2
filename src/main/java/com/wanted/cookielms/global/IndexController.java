package com.wanted.cookielms.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /*
     * 기본 요청시 페이지 이동을 위한 컨트롤러
     * */
    @GetMapping("/")
    public String root(){
        return "index";
    }

    @GetMapping("/instructor")
    public String instructorMainPage(){
        return "instructor/dashboard";
    }

    @GetMapping("/student")
    public String studentMainPage(){
        return "student/dashboard";
    }

    @GetMapping("/admin")
    public String adminMainPage(){
        return "admin/dashboard";
    }

    /*
     * 관리자 권한 설정 체크를 위한 서블릿이다.
     */
}
