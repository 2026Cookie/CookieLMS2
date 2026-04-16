package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import com.wanted.cookielms.domain.lecture.repository.LectureInsRepository;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalTime;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {

    private final LectureInsRepository lectureInsRepository;
    private final LectureStuRepository lectureStuRepository;
    private final ModelMapper modelMapper;
    private final InsFileService insFileService;



    public Page<LectureStuDTO> getMyLectures(Long instructorId, Pageable pageable) {
        Page<InsLecture> myLectures = lectureInsRepository.findByInstructorId(instructorId, pageable);
        return myLectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }


    @Transactional
    public void registLecture(LectureInsDTO dto, Long instructorId) throws IOException {
        String savedName = insFileService.storeFile(dto.getLectureFile(), ".pdf");

        InsLecture lecture = InsLecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .fileSavedName(savedName)
                .fileOriginName(dto.getLectureFile() != null ? dto.getLectureFile().getOriginalFilename() : null)
                .maxCapacity(dto.getMaxCapacity())
                .lectureDay(dto.getLectureDay())
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
            String savedName = insFileService.storeFile(dto.getLectureFile(), ".pdf");
            lecture.updateFileName(dto.getLectureFile().getOriginalFilename(), savedName);
        }

        lecture.updateInfo(
                dto.getTitle(),
                dto.getDescription(),
                dto.getVideoUrl(),
                dto.getMaxCapacity(),
                dto.getLectureDay(),
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

        return dto;
    }


}