package com.wanted.cookielms.domain.assignment.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionStuDTO {
    private MultipartFile uploadFile; // 🌟 5MB 제한 정책이 적용될 업로드 파일
    private String feedback;
}