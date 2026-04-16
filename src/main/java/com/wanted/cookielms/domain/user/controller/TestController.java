package com.wanted.cookielms.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/test-error")
    public String testError() {
        String nullValue = null;
        nullValue.length();  // NullPointerException 발생 → 500 에러
        return "user/main";
    }
}
