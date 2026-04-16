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

        String fileSavedName = saveFile(dto.getLectureFile());

        InsLecture lecture = InsLecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .fileSavedName(fileSavedName)

                .maxCapacity(dto.getMaxCapacity())
                .lectureDay(dto.getLectureDay())
                .startTime(LocalTime.parse(dto.getStartTime()))
                .endTime(LocalTime.parse(dto.getEndTime()))

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


        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("맞지 않는 형식의 파일입니다. PDF만 업로드 해주세요.");
        }


        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("5MB를 초과합니다. 5MB 이하의 PDF를 업로드 해주세요.");
        }


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

        InsLecture lecture = lectureInsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));

        // 2. 파일 수정 처리 (새 파일이 업로드된 경우에만 교체)
        if (dto.getLectureFile() != null && !dto.getLectureFile().isEmpty()) {
            String savedName = saveFile(dto.getLectureFile());
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

        public LectureInsDTO getLectureForEdit(Long id) {
            InsLecture lecture = lectureInsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + id));


            LectureInsDTO dto = modelMapper.map(lecture, LectureInsDTO.class);

            dto.setStartTime(lecture.getStartTime().toString());
            dto.setEndTime(lecture.getEndTime().toString());

            return dto;
        }
    }
