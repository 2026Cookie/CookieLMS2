package com.wanted.cookielms.global;

import com.wanted.cookielms.domain.auth.hander.AuthSuccessHandler;
import com.wanted.cookielms.domain.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthSuccessHandler authSuccessHandler(UserService memberService) {
        return new AuthSuccessHandler(memberService);
    }



    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         AuthSuccessHandler authSuccessHandler) throws Exception {
        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/user/login", "/user/join", "/").permitAll()
                            .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                            .requestMatchers("/user/**").hasRole("USER")
                            .anyRequest().permitAll();
                })
                .formLogin(login -> {
                    login.loginPage("/user/login");
                    login.usernameParameter("loginId");
                    login.passwordParameter("password");
                    login.successHandler(authSuccessHandler);
                    login.failureUrl("/user/login");

                })

                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })

                .csrf(csrf -> csrf.disable());
        return http.build();
    }



}
