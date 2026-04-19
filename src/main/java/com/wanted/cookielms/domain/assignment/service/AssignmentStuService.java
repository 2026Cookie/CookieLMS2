package com.wanted.cookielms.domain.assignment.service;

import com.wanted.cookielms.domain.assignment.dto.AssignmentStuDTO;
import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity;
import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import com.wanted.cookielms.domain.assignment.exception.AssignmentErrorCode;
import com.wanted.cookielms.domain.assignment.exception.AssignmentException;
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository;
import com.wanted.cookielms.domain.assignment.repository.AssignmentSubmissionStuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    // 로컬 파일 저장소 경로 설정 (자동으로 폴더 생성)
    @Value("${file.upload.path:C:/cookielms/uploads/assignments/}")
    private String uploadPath;

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

    @Transactional
    public void submitAssignment(Long assignmentId, Long studentId, MultipartFile file) {
        AssignmentStuEntity assignment = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSIGNMENT_NOT_FOUND));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new AssignmentException(AssignmentErrorCode.SUBMISSION_DEADLINE_PASSED);
        }

        if (file == null || file.isEmpty()) {
            throw new AssignmentException(AssignmentErrorCode.FILE_REQUIRED);
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        List<String> allowedExtensions = Arrays.asList("pdf");
        List<String> allowedMimeTypes = Arrays.asList("application/pdf");

        if (originalFilename != null) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!allowedExtensions.contains(ext) || contentType == null || !allowedMimeTypes.contains(contentType)) {
                throw new AssignmentException(AssignmentErrorCode.INVALID_FILE_FORMAT);
            }
        } else {
            throw new AssignmentException(AssignmentErrorCode.FILE_REQUIRED);
        }

        // 물리적 파일 저장 로직 시작
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            File folder = new File(uploadPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File dest = new File(uploadPath, savedFilename);
            file.transferTo(dest);
        } catch (IOException e) {
            // 일반 에러 대신 커스텀 에러(CRITICAL)를 던ㄷ짐
            throw new AssignmentException(AssignmentErrorCode.FILE_UPLOAD_ERROR);
        }
        // 물리적 파일 저장 로직 끝

        // 임시 파일 ID 생성 (차후 공통 File 테이블 도입 시 교체 필요)
        Long dummyFileId = (long) (file.getOriginalFilename().length() * 100);

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