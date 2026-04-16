package com.wanted.cookielms.domain.lecture.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LectureStuDTO {

    private Long lectureId;
    private String title;
    private String description;
    private Long instructorId;
    private int maxCapacity;
    private int currentEnrollment;
    private String videoUrl;
    private String thumbnail;
    private String materialId;

    // 🌟 ModelMapper가 헷갈리지 않게 전혀 다른 이름으로 변경!
    private boolean userEnrolled;
    private boolean userInstructor;

    public void setUserEnrolled(boolean userEnrolled) {
        this.userEnrolled = userEnrolled;
    }

    public void setUserInstructor(boolean userInstructor) {
        this.userInstructor = userInstructor;
    }
}