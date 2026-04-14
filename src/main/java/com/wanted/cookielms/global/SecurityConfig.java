//package com.wanted.cookielms.global;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//
//
//    @Bean
//    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(auth -> {
//                    auth.anyRequest().permitAll(); // 일단 모든 요청 허용
//                })
//                .csrf(csrf -> csrf.disable());
//        return http.build();
//    }
//
//}
