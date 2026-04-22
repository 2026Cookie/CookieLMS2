package com.wanted.cookielms.global.config;

import com.wanted.cookielms.global.interceptor.ApiTrackingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiTrackingInterceptor apiTrackingInterceptor;

    @Value("${file.upload.path:C:/cookielms/uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/cookielms/uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiTrackingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/favicon.ico",
                        "/error"
                );
    }

}