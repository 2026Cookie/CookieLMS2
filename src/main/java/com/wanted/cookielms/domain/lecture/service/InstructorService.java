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
    private final String uploadPath = "C:/cookielms/uploads/";
    private final LectureStuRepository lectureStuRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void registLecture(LectureInsDTO dto, Long instructorId) throws IOException {

        // 1. 파일 저장 (PDF 검증 포함)
        String fileSavedName = saveFile(dto.getLectureFile());


        InsLecture lecture = InsLecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .fileSavedName(fileSavedName)

                .maxCapacity(dto.getMaxCapacity())
                .lectureDay(dto.getLectureDay())
                .startTime(LocalTime.parse(dto.getStartTime())) // "09:00" 문자열 -> LocalTime 변환
                .endTime(LocalTime.parse(dto.getEndTime()))     // "10:00" 문자열 -> LocalTime 변환

                .instructorId(instructorId)
                .build();

        //  DB 저장
        lectureInsRepository.save(lecture);

    }

    /**
     * 파일 저장 로직 (PDF 형식 및 5MB 용량 체크)
     */
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalName = file.getOriginalFilename();

        // 1. 확장자 체크
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("맞지 않는 형식의 파일입니다. PDF만 업로드 해주세요.");
        }

        // 2. 용량 체크 (5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("5MB를 초과합니다. 5MB 이하의 PDF를 업로드 해주세요.");
        }

        // 3. 실제 폴더에 저장
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + ext;
        File target = new File(uploadPath, savedName);

        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        file.transferTo(target);

        return savedName;
    }


    public Page<LectureStuDTO> getMyLectures(Long instructorId, Pageable pageable) {

        Page<InsLecture> myLectures = lectureInsRepository.findByInstructorId(instructorId, pageable);


        return myLectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }
 }