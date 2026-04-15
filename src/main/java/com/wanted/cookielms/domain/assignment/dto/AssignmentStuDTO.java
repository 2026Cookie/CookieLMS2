package com.wanted.cookielms.domain.assignment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AssignmentStuDTO {
    private Long assignmentId;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private Long lectureId;
}