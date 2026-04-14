package com.wanted.cookielms.domain.lecture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureStuDTO {
    private Long id;
    private String title;
    private Long instructorId;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private String materialId;
}