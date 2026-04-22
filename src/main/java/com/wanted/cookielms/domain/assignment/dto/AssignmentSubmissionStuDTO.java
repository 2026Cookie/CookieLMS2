package com.wanted.cookielms.domain.assignment.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionStuDTO {
    private MultipartFile uploadFile;
    private String feedback;
}