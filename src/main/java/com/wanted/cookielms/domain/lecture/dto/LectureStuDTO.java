package com.wanted.cookielms.domain.lecture.dto; // 🌟 패키지 경로 수정

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
    private int currentEnrollment; // 🌟 엔티티와 이름 맞춤 (언더바 제거)
    private String videoUrl;
    private String thumbnail;
    private String materialId;

    private boolean isEnrolled;

    public void setEnrolled(boolean enrolled) {
        this.isEnrolled = enrolled;
    }
}