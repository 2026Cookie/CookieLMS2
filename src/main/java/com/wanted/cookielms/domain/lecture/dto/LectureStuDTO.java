package com.wanted.cookielms.domain.lecture.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 모든 필드 생성자 (Lombok)
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

    private String instructorName; // 강사 이름

    // 과제 등록을 위한 코드
    private Long assignmentId;

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    // 🚀 [추가] Repository의 상세조회 '한 방 쿼리'를 위한 전용 생성자!
    // 쿼리 순서: l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail, l.videoUrl, l.materialId
    public LectureStuDTO(Long lectureId, String title, String instructorName,
                         int currentEnrollment, int maxCapacity, String thumbnail,
                         String videoUrl, String materialId) {
        this.lectureId = lectureId;
        this.title = title;
        this.instructorName = instructorName;
        this.currentEnrollment = currentEnrollment;
        this.maxCapacity = maxCapacity;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.materialId = materialId;
    }

    // Setter 메서드들 (필요시 사용)
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