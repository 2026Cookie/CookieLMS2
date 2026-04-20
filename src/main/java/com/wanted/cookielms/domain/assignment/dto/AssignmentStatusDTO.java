package com.wanted.cookielms.domain.assignment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class AssignmentStatusDTO {
    // 1. 학생 정보
    private Long studentId;
    private String name;
    private String email;
    private String phone;

    // 2. 제출 정보
    private LocalDateTime submittedAt;
    private Long submissionId;



    public AssignmentStatusDTO(Long studentId, String name, String email, String phone, LocalDateTime submittedAt, Long submissionId) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.submittedAt = submittedAt;
        this.submissionId = submissionId;
    }

    public boolean isSubmitted() {
        return submissionId != null;
    }
}