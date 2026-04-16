package com.wanted.cookielms.domain.lecture.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureStuEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long lectureId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "lecture_day", nullable = false, length = 10)
    private String lectureDay;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "material_id", length = 500)
    private String materialId; // 화면에서 썸네일 경로로 쓸 컬럼

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private Long instructorId;

    public void increaseEnrollment() {
        this.currentEnrollment++;
    }

    public void decreaseEnrollment() {
        if (this.currentEnrollment > 0) {
            this.currentEnrollment--;
        }
    }
}