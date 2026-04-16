package com.wanted.cookielms.domain.lecture.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "lecture")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "material_id") // 컬럼명과 매핑
    private String fileSavedName;

    @Column(name = "file_origin_name")
    private String fileOriginName;


    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_enrollment")
    private Integer currentEnrollment = 0; // 등록 시 기본값 0

    @Column(length = 20)
    private String status = "OPEN"; // 등록 시 기본값 OPEN

    @Column(name = "lecture_day")
    private String lectureDay;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "user_id")
    private Long instructorId = 2L; // "// TODO: 로그인 연동 후 세션 기반으로 변경 예정"

    @Builder
    private InsLecture(String title, String description, String videoUrl, String fileSavedName, String fileOriginName,
                       Integer maxCapacity, String lectureDay, LocalTime startTime, LocalTime endTime,Long instructorId) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.fileSavedName = fileSavedName;

        this.maxCapacity = maxCapacity;
        this.lectureDay = lectureDay;
        this.startTime = startTime;
        this.endTime = endTime;
        //  기본값 설정
        this.currentEnrollment = 0;
        this.instructorId = instructorId;
        this.status = "OPEN";

    }
    public void updateInfo(String title, String description, String videoUrl,
                           Integer maxCapacity, String lectureDay,
                           java.time.LocalTime startTime, java.time.LocalTime endTime) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.maxCapacity = maxCapacity;
        this.lectureDay = lectureDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 파일 정보 수정 메서드
    public void updateFileName(String fileOriginName, String fileSavedName) {
        this.fileOriginName = fileOriginName;
        this.fileSavedName = fileSavedName;
    }
}
