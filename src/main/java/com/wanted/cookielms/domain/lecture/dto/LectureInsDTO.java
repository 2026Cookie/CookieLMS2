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

    private Long lectureId;
    private String title;
    private String description;
    private MultipartFile lectureFile;
    private String videoUrl;
    private Integer maxCapacity;
    private String lectureDay;
    private String startTime;
    private String endTime;



}
