package com.wanted.cookielms.global;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
    public ResponseEntity<Void> keepAlive(HttpSession session) {
        session.setAttribute("sessionExpireAt",
                System.currentTimeMillis() + session.getMaxInactiveInterval() * 1000L);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/api/session/remaining")
    public ResponseEntity<Map<String, Long>> sessionRemaining(HttpSession session) {
        Long expireAt = (Long) session.getAttribute("sessionExpireAt");
        if (expireAt == null) {
            return ResponseEntity.ok(Map.of("remaining", 0L));
        }
        long remaining = (expireAt - System.currentTimeMillis()) / 1000;
        return ResponseEntity.ok(Map.of("remaining", Math.max(remaining, 0)));
    }

    /*
     * 관리자 권한 설정 체크를 위한 서블릿이다.
     */
}
