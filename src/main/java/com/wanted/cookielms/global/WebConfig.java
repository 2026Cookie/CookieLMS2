package com.wanted.cookielms.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [추가] 브라우저가 /uploads/파일명 으로 요청하면 C:/cookielms/uploads/ 폴더에서 파일을 찾아줌
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/cookielms/uploads/");
    }
}