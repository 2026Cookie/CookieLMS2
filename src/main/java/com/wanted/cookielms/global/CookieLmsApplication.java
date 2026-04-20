package com.wanted.cookielms.global;

import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.time.LocalTime;

@SpringBootApplication
public class CookieLmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookieLmsApplication.class, args);
    }

}