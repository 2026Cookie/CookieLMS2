package com.wanted.cookielms.domain.lecture.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat; // 🌟 추가
import java.time.LocalDateTime; // 🌟 추가

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
    private MultipartFile thumbnail;
    private String thumbnailPath;

    // 🌟 강사가 과제를 같이 등록할 때 사용할 필드 3개 추가!
    private String assignmentTitle;
    private String assignmentContent;

    // HTML의 <input type="datetime-local">과 자바의 LocalDateTime을 예쁘게 연결해주는 마법!
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime assignmentDueDate;
}
