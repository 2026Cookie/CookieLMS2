package com.wanted.cookielms.domain.assignment.service;

import com.wanted.cookielms.domain.assignment.dto.AssignmentStuDTO;
import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity;
import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository;
import com.wanted.cookielms.domain.assignment.repository.AssignmentSubmissionStuRepository;
import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.error.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentStuService {

    private final AssignmentStuRepository assignmentStuRepository;
    private final AssignmentSubmissionStuRepository assignmentSubmissionStuRepository;

    /**
     * 과제 접근 권한 검증 (AOP에서 호출)
     */
    public void validateAssignmentAccess(Long assignmentId, Long userId) {
        // 1. 과제 존재 여부 확인 (IDOR 방지 기초)
        assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

        // 2. [확장 포인트] 해당 학생이 이 과제가 속한 강의를 수강 중인지 검증하는 로직이 여기에 들어갑니다.
    }

    public AssignmentStuDTO getAssignment(Long assignmentId) {
        AssignmentStuEntity entity = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

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
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST); // 예외 타입 일관화
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        List<String> allowedExtensions = Arrays.asList("pdf");
        List<String> allowedMimeTypes = Arrays.asList("application/pdf");

        if (originalFilename != null) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!allowedExtensions.contains(ext) || contentType == null || !allowedMimeTypes.contains(contentType)) {
                throw new ApplicationException(ErrorCode.BAD_REQUEST);
            }
        } else {
            throw new ApplicationException(ErrorCode.BAD_REQUEST);
        }

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