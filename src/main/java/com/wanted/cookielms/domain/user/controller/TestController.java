package com.wanted.cookielms.domain.user.controller;

import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    // 1. 500 - NullPointerException (handleUnhandledException)
    @GetMapping("/error-500")
    public String test500() {
        String nullValue = null;
        nullValue.length();
        return "user/main";
    }

    // 2. 비즈니스 예외 (handleApplicationException)
    @GetMapping("/error-biz")
    public String testBizError() {
        throw new ApplicationException(
                HttpStatus.BAD_REQUEST, "BIZ_001", "테스트용 비즈니스 예외입니다.", ErrorSeverity.WARNING
        ) {};
    }

    // 3. 400 - @Valid 바인딩 에러 (handleValidationException)
    @PostMapping("/error-400")
    public String test400(@Valid @RequestBody TestDto dto) {
        return "user/main";
    }

    // 4. 405 - GET만 있는 곳에 POST → 자동 발생 (handleMethodNotSupportedException)
    //    테스트: POST /test/error-405 호출
    @GetMapping("/error-405")
    public String test405() {
        return "user/main";
    }

    // 5. 403 - 권한 없는 페이지 접근 (CustomAccessDeniedHandler)
    //    테스트: 일반 USER 계정으로 /instructor/** 접근
    //    별도 엔드포인트 불필요 — SecurityConfig에서 이미 역할 체크함

    // 6. 401 - 미인증 접근 (CustomAuthenticationEntryPoint)
    //    테스트: 로그아웃 상태에서 /user/main 접근
    //    별도 엔드포인트 불필요 — SecurityConfig에서 이미 체크함

    @Getter
    @Setter
    static class TestDto {
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
    }
}