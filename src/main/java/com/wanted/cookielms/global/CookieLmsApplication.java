package com.wanted.cookielms.global;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CookieLmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookieLmsApplication.class, args);

    }

}