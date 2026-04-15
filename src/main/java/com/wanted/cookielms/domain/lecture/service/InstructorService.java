package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import com.wanted.cookielms.domain.lecture.repository.LectureInsRepository;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@Setter
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
    @Transactional //
    public void updateLecture(Long id, LectureInsDTO dto) throws IOException {
        // 1. 기존 강의 엔티티 조회 (DB에서 가져오기)
        InsLecture lecture = lectureInsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

        // 2. 파일 수정 처리 (새 파일이 업로드된 경우에만 교체)
        if (dto.getLectureFile() != null && !dto.getLectureFile().isEmpty()) {
            String savedName = saveFile(dto.getLectureFile());
            lecture.updateFileName(dto.getLectureFile().getOriginalFilename(), savedName);
        }

        // 3. 정보 업데이트 (JPA 변경 감지 활용)
        // LocalTime.parse를 사용해 DTO의 String을 시간 객체로 변환합니다.
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

        public LectureInsDTO getLectureForEdit(Long id) {
            InsLecture lecture = lectureInsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

            // ModelMapper를 사용하여 DTO로 변환하거나 직접 빌더를 사용합니다.
            LectureInsDTO dto = modelMapper.map(lecture, LectureInsDTO.class);

            // LocalTime을 String으로 변환해서 넣어줍니다. (HTML input type="time" 바인딩용)
            dto.setStartTime(lecture.getStartTime().toString());
            dto.setEndTime(lecture.getEndTime().toString());

            return dto;
        }
    }
