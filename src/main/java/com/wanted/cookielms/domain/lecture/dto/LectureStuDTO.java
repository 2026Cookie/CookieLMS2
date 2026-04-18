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

    private boolean userEnrolled;
    private boolean userInstructor;

    private String instructorName; // 강사 이름 추가

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setUserEnrolled(boolean userEnrolled) {
        this.userEnrolled = userEnrolled;
    }

    public void setUserInstructor(boolean userInstructor) {
        this.userInstructor = userInstructor;
    }
}