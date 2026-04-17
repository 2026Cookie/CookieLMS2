package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import com.wanted.cookielms.domain.lecture.enums.LectureDay;
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
    private final ModelMapper modelMapper;



    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    public Page<LectureStuDTO> getMyLectures(Long instructorId, Pageable pageable) {
        Page<InsLecture> myLectures = lectureInsRepository.findByInstructorId(instructorId, pageable);
        return myLectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }


    @Transactional
    public void registLecture(LectureInsDTO dto, Long instructorId) throws IOException {
        String savedName = storeFile(dto.getLectureFile(), ".pdf");

        InsLecture lecture = InsLecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .fileSavedName(savedName)
                .fileOriginName(dto.getLectureFile() != null ? dto.getLectureFile().getOriginalFilename() : null)
                .maxCapacity(dto.getMaxCapacity())
                .lectureDay(LectureDay.valueOf(dto.getLectureDay()))
                .startTime(LocalTime.parse(dto.getStartTime()))
                .endTime(LocalTime.parse(dto.getEndTime()))
                .instructorId(instructorId)
                .build();

        lectureInsRepository.save(lecture);
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
            String savedName = storeFile(dto.getLectureFile(), ".pdf");
            lecture.updateFileName(dto.getLectureFile().getOriginalFilename(), savedName);
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

        return dto;
    }
    private String storeFile(MultipartFile file, String allowedExtension) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();

        // 1. 확장자 검증
        if (originalName == null || !originalName.toLowerCase().endsWith(allowedExtension)) {
            throw new IllegalArgumentException(allowedExtension.toUpperCase() + " 형식의 파일만 업로드 가능합니다.");
        }

        // 2. 용량 검증 (5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 용량은 5MB를 초과할 수 없습니다.");
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
        file.transferTo(targetFile); // 이제 임시 폴더가 아닌 프로젝트 폴더 내 uploads에 저장됩니다.

        return savedName;
    }
}