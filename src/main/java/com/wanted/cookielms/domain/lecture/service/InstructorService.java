package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity; // 🌟 추가
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository; // 🌟 추가
import com.wanted.cookielms.domain.assignment.dto.AssignmentStatusDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import com.wanted.cookielms.domain.lecture.enums.LectureDay;
import com.wanted.cookielms.domain.lecture.repository.LectureInsRepository;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import com.wanted.cookielms.global.aop.BussinessServiceLogging;
import com.wanted.cookielms.global.aop.FileValidation.FileValidation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wanted.cookielms.domain.assignment.repository.AssignmentSubmissionStuRepository;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {

    private final LectureInsRepository lectureInsRepository;
    private final LectureStuRepository lectureStuRepository;
    private final AssignmentStuRepository assignmentStuRepository; // 🌟 과제 저장소 주입 추가!
    private final ModelMapper modelMapper;
    private final InsFileService insFileService;
    private final AssignmentSubmissionStuRepository assignmentSubmissionStuRepository;



    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    public Page<LectureStuDTO> getMyLectures(Long instructorId, Pageable pageable) {
        // 1. 강사가 등록한 강의 목록을 DB에서 가져옵니다.
        Page<InsLecture> myLectures = lectureInsRepository.findByInstructorId(instructorId, pageable);


        return myLectures.map(entity -> {
             LectureStuDTO dto = modelMapper.map(entity, LectureStuDTO.class);


            dto.setLectureId(entity.getId());


            assignmentStuRepository.findByLectureId(entity.getId())
                    .ifPresent(assignment -> {
                        dto.setAssignmentId(assignment.getId());;
                    });

            // 🌟 4) 정보가 꽉 찬 dto를 반환합니다. (이게 없으면 에러가 납니다!)
            return dto;
        });
    }

    @BussinessServiceLogging
    @Transactional
    public void registLecture(LectureInsDTO dto, Long instructorId) throws IOException {
        String savedPdfName = insFileService.storeFile(dto.getLectureFile(), ".pdf");
        String savedThumbnailName = insFileService.storeFile(dto.getThumbnail(), ".jpg", ".png", ".jpeg", ".gif");

        InsLecture lecture = InsLecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .fileSavedName(savedPdfName)
                .thumbnail(savedThumbnailName)
                .fileOriginName(dto.getLectureFile() != null ? dto.getLectureFile().getOriginalFilename() : null)
                .maxCapacity(dto.getMaxCapacity())
                .lectureDay(LectureDay.valueOf(dto.getLectureDay()))
                .startTime(LocalTime.parse(dto.getStartTime()))
                .endTime(LocalTime.parse(dto.getEndTime()))
                .instructorId(instructorId)
                .build();

        // 🌟 강의 먼저 저장 (저장되면서 ID가 자동 생성됨)
        InsLecture savedLecture = lectureInsRepository.save(lecture);

        // 🌟 추가된 부분: 과제 제목이 입력되어 있다면 과제도 생성해서 저장한다!
        if (dto.getAssignmentTitle() != null && !dto.getAssignmentTitle().trim().isEmpty()) {
            AssignmentStuEntity assignment = AssignmentStuEntity.builder()
                    .title(dto.getAssignmentTitle())
                    // 내용이 비어있으면 기본값, 있으면 입력값 세팅
                    .content((dto.getAssignmentContent() != null && !dto.getAssignmentContent().trim().isEmpty()) ? dto.getAssignmentContent() : "과제 내용이 없습니다.")
                    .dueDate(dto.getAssignmentDueDate())
                    .lectureId(savedLecture.getId()) // 🌟 정답! "id를 가져와줘!"
                    .build();

            assignmentStuRepository.save(assignment);
        }
    }


    @Transactional
    public void updateLecture(Long id, LectureInsDTO dto, Long instructorId) throws IOException {
        // 수정 로직은 이름이 updateLecture여야 합니다.
        InsLecture lecture = lectureInsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

        if (!lecture.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("본인의 강의만 수정할 수 있습니다.");
        }

        if (dto.getLectureFile() != null && !dto.getLectureFile().isEmpty()) {
            String savedPdfName = insFileService.storeFile(dto.getLectureFile(), ".pdf");
            lecture.updateFileName(dto.getLectureFile().getOriginalFilename(), savedPdfName);
        }

        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            String savedThumbnailName = insFileService.storeFile(dto.getThumbnail(), ".jpg", ".png", ".jpeg", ".gif");
            lecture.updateThumbnail(savedThumbnailName);
        }

        lecture.updateInfo(
                dto.getTitle(),
                dto.getDescription(),
                dto.getVideoUrl(),
                dto.getMaxCapacity(),
                LectureDay.valueOf(dto.getLectureDay()),
                LocalTime.parse(dto.getStartTime()),
                LocalTime.parse(dto.getEndTime())
        );

        AssignmentStuEntity assignment = assignmentStuRepository.findByLectureId(id)
                .orElseGet(() -> AssignmentStuEntity.builder().lectureId(id).build());

        if (dto.getAssignmentTitle() != null && !dto.getAssignmentTitle().trim().isEmpty()) {
            assignment.updateAssignment(
                    dto.getAssignmentTitle(),
                    dto.getAssignmentContent(),
                    dto.getAssignmentDueDate()
            );
            assignmentStuRepository.save(assignment);
        }
    }
    public LectureInsDTO getLectureForEdit(Long id, Long instructorId) {
        InsLecture lecture = lectureInsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

        if (!lecture.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("본인의 강의만 조회할 수 있습니다.");
        }

        LectureInsDTO dto = modelMapper.map(lecture, LectureInsDTO.class);

        assignmentStuRepository.findByLectureId(id).ifPresent(assignment -> {
            dto.setAssignmentTitle(assignment.getTitle());
            dto.setAssignmentContent(assignment.getContent());
            dto.setAssignmentDueDate(assignment.getDueDate());
        });

        dto.setStartTime(lecture.getStartTime().toString());
        dto.setEndTime(lecture.getEndTime().toString());
        dto.setLectureDay(lecture.getLectureDay().name());
        dto.setThumbnailPath(lecture.getThumbnail());
        AssignmentStuEntity assignment = assignmentStuRepository.findByLectureId(id).orElse(null);
        if (assignment != null) {
            dto.setAssignmentTitle(assignment.getTitle());
            dto.setAssignmentContent(assignment.getContent());
            dto.setAssignmentDueDate(assignment.getDueDate());
        }

        return dto;
    }
    public Map<String, Object> getAssignmentDashboard(Long assignmentId, Long lectureId) {
        List<AssignmentStatusDTO> statusList = assignmentSubmissionStuRepository.findSubmissionStatusByAssignmentAndLecture(assignmentId, lectureId);

        AssignmentStuEntity assignment = assignmentStuRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과제입니다. ID: " + assignmentId));

        long totalStudents = statusList.size();
        long submittedCount = statusList.stream().filter(AssignmentStatusDTO::isSubmitted).count();

        Map<String, Object> result = new HashMap<>();
        result.put("students", statusList);
        result.put("assignment", assignment);
        result.put("totalCount", totalStudents);
        result.put("submittedCount", submittedCount);
        result.put("unsubmittedCount", totalStudents - submittedCount);

        return result;
    }

}
