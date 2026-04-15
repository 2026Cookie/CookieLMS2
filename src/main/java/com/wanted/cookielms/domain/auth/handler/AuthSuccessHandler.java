package com.wanted.cookielms.domain.auth.handler;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    public AuthSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String loginId = authentication.getName();
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        Role role = authDetails.getLoginUserDTO().getRole();

        if (role == Role.INSTRUCTOR) {
            response.sendRedirect("/instructor/main");
        } else if (role == Role.USER) {
            response.sendRedirect("/user/main");
        } else {
            response.sendRedirect("/");
        }

    }

}
