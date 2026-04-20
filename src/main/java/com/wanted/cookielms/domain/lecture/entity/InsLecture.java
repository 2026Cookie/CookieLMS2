package com.wanted.cookielms.domain.lecture.entity;

import com.wanted.cookielms.domain.lecture.enums.LectureDay;
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

    @Enumerated(EnumType.STRING) //
    @Column(name = "lecture_day")
    private LectureDay lectureDay;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "user_id")
    private Long instructorId;

    @Column(name = "thumbnail") // DB에 저장될 컬럼명
    private String thumbnail;



    @Builder
    private InsLecture(String title, String description, String videoUrl, String fileSavedName, String fileOriginName,String thumbnail,
                       Integer maxCapacity, LectureDay lectureDay, LocalTime startTime, LocalTime endTime, Long instructorId) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.fileSavedName = fileSavedName;
        this.fileOriginName = fileOriginName;
        this.thumbnail = thumbnail;
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
                           Integer maxCapacity, LectureDay lectureDay,
                           java.time.LocalTime startTime, java.time.LocalTime endTime) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.maxCapacity = maxCapacity;
        this.lectureDay = lectureDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public void updateFileName(String fileOriginName, String fileSavedName) {
        this.fileOriginName = fileOriginName;
        this.fileSavedName = fileSavedName;
    }
    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
