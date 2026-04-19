package com.wanted.cookielms.global;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "instructor/main";
    }

    @GetMapping("/student")
    public String studentMainPage(){
        return "user/main";
    }

    @GetMapping("/admin")
    public String adminMainPage(){
        return "admin/dashboard";
    }

    @ResponseBody
    @GetMapping("/api/session/keep-alive")
    public ResponseEntity<Void> keepAlive() {
        return ResponseEntity.ok().build();
    }

    /*
     * 관리자 권한 설정 체크를 위한 서블릿이다.
     */
}
