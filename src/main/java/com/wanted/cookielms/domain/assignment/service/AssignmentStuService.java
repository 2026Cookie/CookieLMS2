package com.wanted.cookielms.domain.assignment.service;

import com.wanted.cookielms.domain.assignment.dto.AssignmentStuDTO;
import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity;
import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import com.wanted.cookielms.domain.assignment.exception.AssignmentErrorCode;
import com.wanted.cookielms.domain.assignment.exception.AssignmentException;
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository;
import com.wanted.cookielms.domain.assignment.repository.AssignmentSubmissionStuRepository;
import com.wanted.cookielms.global.aop.BussinessServiceLogging;
import com.wanted.cookielms.global.aop.FileValidation.FileValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentStuService {

    private final AssignmentStuRepository assignmentStuRepository;
    private final AssignmentSubmissionStuRepository assignmentSubmissionStuRepository;

    // 🌟 1. 과제가 저장될 인텔리제이 내부 공통 경로 설정
    @Value("${file.upload.path:uploads/}")
    private String baseUploadPath;

    /**
     * 과제 접근 권한 검증 (AOP에서 호출)
     */
    public void validateAssignmentAccess(Long assignmentId, Long userId) {
        assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSIGNMENT_NOT_FOUND));
    }

    public AssignmentStuDTO getAssignment(Long assignmentId) {
        AssignmentStuEntity entity = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSIGNMENT_NOT_FOUND));

        return AssignmentStuDTO.builder()
                .assignmentId(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .dueDate(entity.getDueDate())
                .lectureId(entity.getLectureId())
                .build();
    }

    @BussinessServiceLogging
    @FileValidation(maxSize = 5, allowedExtensions = {".pdf"}, allowedMimeTypes = {"application/pdf"})
    @Transactional
    public void submitAssignment(Long assignmentId, Long studentId, MultipartFile file) {
        AssignmentStuEntity assignment = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSIGNMENT_NOT_FOUND));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new AssignmentException(AssignmentErrorCode.SUBMISSION_DEADLINE_PASSED);
        }

        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            Path uploadDir = Paths.get(baseUploadPath, "assignments").toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path target = uploadDir.resolve(savedFilename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AssignmentException(AssignmentErrorCode.FILE_UPLOAD_ERROR);
        }

        Long dummyFileId = (long) (originalFilename.length() * 100);

        assignmentSubmissionStuRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .ifPresentOrElse(
                        existingSubmission -> existingSubmission.updateFileId(dummyFileId),
                        () -> {
                            AssignmentSubmissionStuEntity newSubmission = AssignmentSubmissionStuEntity.builder()
                                    .assignmentId(assignmentId)
                                    .studentId(studentId)
                                    .fileId(dummyFileId)
                                    .build();
                            assignmentSubmissionStuRepository.save(newSubmission);
                        }
                );
    }
}