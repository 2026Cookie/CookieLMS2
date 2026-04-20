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

    @Bean
    public CommandLineRunner initData(LectureStuRepository repository) {
        return args -> {
            if (repository.count() < 1000) {
                System.out.println("⏳ [데이터 생성] 2,000건 생성 중... (약 5~10초 소요)");

                for (int i = 1; i <= 2000; i++) {
                    // 외부 라이브러리 없이 강제로 인스턴스 생성
                    LectureStuEntity lecture = org.springframework.objenesis.ObjenesisHelper.newInstance(LectureStuEntity.class);
                    String title = (i <= 100) ? "자바 마스터 클래스 " + i : "일반 강의 " + i;

                    // 외부 도구 없이 순수 자바 리플렉션으로 값 넣기
                    setFieldValue(lecture, "title", title);
                    setFieldValue(lecture, "description", "성능 테스트 데이터 " + i);
                    setFieldValue(lecture, "maxCapacity", 30);
                    setFieldValue(lecture, "currentEnrollment", 0);
                    setFieldValue(lecture, "status", "OPEN");
                    setFieldValue(lecture, "lectureDay", "월요일");
                    setFieldValue(lecture, "startTime", LocalTime.of(9, 0));
                    setFieldValue(lecture, "endTime", LocalTime.of(12, 0));
                    setFieldValue(lecture, "instructorId", 1L);

                    repository.save(lecture);
                }
                System.out.println("🚀 [완료] 2,000건 삽입 성공! 이제 대시보드를 보세요.");
            }
        };
    }

    // 값 주입을 도와주는 보조 메서드
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}