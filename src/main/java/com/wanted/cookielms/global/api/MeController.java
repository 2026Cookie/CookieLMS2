package com.wanted.cookielms.global.api;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getMe(@AuthenticationPrincipal AuthDetails authDetails) {
        String nickname = authDetails.getLoginUserDTO().getNickname();
        return ResponseEntity.ok(Map.of("nickname", nickname != null ? nickname : ""));
    }
}
