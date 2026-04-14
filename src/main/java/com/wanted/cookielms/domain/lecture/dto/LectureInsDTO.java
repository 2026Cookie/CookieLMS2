package com.wanted.cookielms.domain.lecture.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LectureInsDTO {

    private String title;           // 강의 제목
    private String description;     // 강의 설명
    private MultipartFile lectureFile;
    private String videoUrl;// 수업 자료 (HTML의 name="lectureFile"와 매칭)

    private Integer maxCapacity;
    private String lectureDay;
    private String startTime;
    private String endTime;
}