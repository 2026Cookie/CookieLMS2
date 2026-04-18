package com.wanted.cookielms.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyLectureListDTO {
    private Long lectureId;
    private String title;
    private String instructorName;
    private int currentEnrollment;
    private int maxCapacity;
    private String thumbnail;
}
