package com.wanted.cookielms.domain.assignment.service;

import com.wanted.cookielms.domain.assignment.dto.AssignmentStuDTO;
import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity;
import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository;
import com.wanted.cookielms.domain.assignment.repository.AssignmentSubmissionStuRepository;
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

    public AssignmentStuDTO getAssignment(Long assignmentId) {
        AssignmentStuEntity entity = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 과제가 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("등록된 과제가 없습니다."));

        // 마감 기한 체크
        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("과제 제출 기한이 지났습니다.");
        }

        // 보안: 확장자 및 MIME Type 이중 체크 (Data Contamination 방지)
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType(); // 파일의 진짜 타입 (MIME)

        // 허용할 화이트리스트(Whitelist) 정의
        List<String> allowedExtensions = Arrays.asList("pdf");
        List<String> allowedMimeTypes = Arrays.asList("application/pdf");

        if (originalFilename != null) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            // 확장자가 없거나, 허용되지 않은 확장자이거나, 내용물(MIME)이 조작된 경우 차단
            if (!allowedExtensions.contains(ext) || contentType == null || !allowedMimeTypes.contains(contentType)) {
                throw new IllegalArgumentException("PDF만 제출 가능합니다.");
            }
        } else {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        Long dummyFileId = (long) (file.getOriginalFilename().length() * 100);

        // Upsert 로직 (있으면 덮어쓰고, 없으면 새로 저장)
        assignmentSubmissionStuRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .ifPresentOrElse(
                        // 1. 이미 제출한 내역이 있으면? -> 파일 ID만 새로 업데이트! (DB에 행이 늘어나지 않음)
                        existingSubmission -> existingSubmission.updateFileId(dummyFileId),

                        // 2. 제출한 내역이 없으면? -> 새로 만들어서 INSERT!
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