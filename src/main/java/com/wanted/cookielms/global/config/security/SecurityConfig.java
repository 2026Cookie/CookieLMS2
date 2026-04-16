package com.wanted.cookielms.global.config.security;

import com.wanted.cookielms.domain.auth.handler.AuthFailureHandler;
import com.wanted.cookielms.domain.auth.handler.AuthSuccessHandler;
import com.wanted.cookielms.domain.user.service.UserService;
import com.wanted.cookielms.global.config.security.handler.CustomAccessDeniedHandler;
import com.wanted.cookielms.global.config.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 💡 추가된 핸들러들을 생성자 주입으로 받습니다.
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 💡 AuthSuccessHandler는 기존처럼 빈으로 관리 (UserService 주입)
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
                                    "/",
                                    "/error",           // 💡 중요: 에러 페이지 접근 허용
                                    "/css/**",          // 💡 중요: 에러 페이지 디자인 유지
                                    "/js/**",
                                    "/images/**",
                                    "/favicon.ico",
                                    "/user/find_password",
                                    "/user/reset_password",
                                    "/user/find_id"

                            ).permitAll()
                            .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                            .requestMatchers("/user/**").hasRole("USER")
                            .anyRequest().authenticated(); // 나머지는 인증 필요
                })

                // 2. 로그인 설정
                .formLogin(login -> {
                    login.loginPage("/user/login");
                    login.usernameParameter("loginId");
                    login.passwordParameter("password");
                    login.successHandler(authSuccessHandler);
                    login.failureHandler(authFailureHandler); // 💡 고정 Url 대신 커스텀 핸들러 연결
                })

                // 3. 예외 처리 설정 (사각지대 방어)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint); // 401 에러
                    exception.accessDeniedHandler(accessDeniedHandler);           // 403 에러
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