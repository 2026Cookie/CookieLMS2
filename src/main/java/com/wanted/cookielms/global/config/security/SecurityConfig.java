package com.wanted.cookielms.global.config.security;

import com.wanted.cookielms.domain.auth.handler.AuthFailureHandler;
import com.wanted.cookielms.domain.auth.handler.AuthSuccessHandler;
import com.wanted.cookielms.domain.auth.service.AuthService;
import com.wanted.cookielms.domain.user.service.UserService;
import com.wanted.cookielms.global.config.security.handler.CustomAccessDeniedHandler;
import com.wanted.cookielms.global.config.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthFailureHandler authFailureHandler;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthSuccessHandler authSuccessHandler(UserService userService) {
        return new AuthSuccessHandler(userService);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         AuthSuccessHandler authSuccessHandler) throws Exception {
        http
                // 1. 접근 권한 설정
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                                    "/user/login",
                                    "/user/join",
                                    "/user/joinsuccess",
                                    "/user/find_id",
                                    "/",
                                    "/error",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
                                    "/favicon.ico",
                                    "/user/find_password",
                                    "/user/reset_password"
                            ).permitAll()
                            .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                            .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                            .requestMatchers("/user/mypage/**", "/user/mypage", "/user/verify-password").hasAnyRole("USER", "INSTRUCTOR")
                            .requestMatchers("/user/**").hasRole("USER")

                            .anyRequest().authenticated(); // 나머지는 인증 필요
                })

                // 2. 로그인 설정
                .formLogin(login -> {
                    login.loginPage("/user/login");
                    login.usernameParameter("loginId");
                    login.passwordParameter("password");
                    login.successHandler(authSuccessHandler);
                    login.failureHandler(authFailureHandler);
                })

                // 3. 예외 처리 설정
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint);
                    exception.accessDeniedHandler(accessDeniedHandler);
                })

                // 4. 로그아웃 설정
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                    logout.invalidateHttpSession(true);
                })

                // 5. 보안 설정
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}