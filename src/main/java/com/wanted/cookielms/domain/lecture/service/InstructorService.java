package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity; // 🌟 추가
import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository; // 🌟 추가
import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import com.wanted.cookielms.domain.lecture.enums.LectureDay;
import com.wanted.cookielms.domain.lecture.exception.LectureErrorCode;
import com.wanted.cookielms.domain.lecture.exception.LectureException;
import com.wanted.cookielms.domain.lecture.repository.LectureInsRepository;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {

    private final LectureInsRepository lectureInsRepository;
    private final LectureStuRepository lectureStuRepository;
    private final AssignmentStuRepository assignmentStuRepository; // 🌟 과제 저장소 주입 추가!
    private final ModelMapper modelMapper;
    private final InsFileService insFileService;



    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    public Page<LectureStuDTO> getMyLectures(Long instructorId, Pageable pageable) {
        Page<InsLecture> myLectures = lectureInsRepository.findByInstructorId(instructorId, pageable);
        return myLectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }


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
        if (dto.getAssignmentTitle() != null && !dto.getAssignmentTitle().trim().isEmpty()) {
            String content = (dto.getAssignmentContent() != null && !dto.getAssignmentContent().trim().isEmpty())
                    ? dto.getAssignmentContent() : "과제 내용이 없습니다.";

            // 1. 해당 강의 번호로 기존 과제가 있는지 DB에서 찾아옵니다.
            AssignmentStuEntity existingAssignment = assignmentStuRepository.findByLectureId(id).orElse(null);

            if (existingAssignment != null) {
                // 2-A. 기존 과제가 있다면 수정 (※ AssignmentStuEntity에 update 메서드를 만들어주세요!)
                existingAssignment.update(dto.getAssignmentTitle(), content, dto.getAssignmentDueDate());
            } else {
                // 2-B. 예전엔 과제가 없었는데 이번에 새로 추가했다면 새로 만들어서 저장
                AssignmentStuEntity newAssignment = AssignmentStuEntity.builder()
                        .title(dto.getAssignmentTitle())
                        .content(content)
                        .dueDate(dto.getAssignmentDueDate())
                        .lectureId(id)
                        .build();
                assignmentStuRepository.save(newAssignment);
            }
        }

    }

    public LectureInsDTO getLectureForEdit(Long id, Long instructorId) {
        InsLecture lecture = lectureInsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

        if (!lecture.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("본인의 강의만 조회할 수 있습니다.");
        }

        LectureInsDTO dto = modelMapper.map(lecture, LectureInsDTO.class);
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

    private String storeFile(MultipartFile file, String allowedExtension) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();

        // 1. 확장자 검증
        if (originalName == null || !originalName.toLowerCase().endsWith(allowedExtension)) {
            throw new LectureException(LectureErrorCode.INVALID_FILE_EXTENSION);
        }

        // 2. 용량 검증 (20MB)
        long maxSize = 20 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new LectureException(LectureErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 3. 고유 파일명 생성
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + ext;

        // 4. 물리적 저장
        File targetDir = new File(uploadPath).getAbsoluteFile();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File targetFile = new File(targetDir, savedName);
        file.transferTo(targetFile);

        return savedName;
    }
}
