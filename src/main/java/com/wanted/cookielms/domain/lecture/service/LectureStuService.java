package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureStuService {

    private final LectureStuRepository lectureStuRepository;
    private final ModelMapper modelMapper;

    public Page<LectureStuDTO> getAllLectures(String keyword, Pageable pageable) {
        Page<LectureStuEntity> lectures;
        if (keyword == null || keyword.trim().isEmpty()) {
            lectures = lectureStuRepository.findAll(pageable);
        } else {
            lectures = lectureStuRepository.findByTitleContaining(keyword, pageable);
        }
        return lectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }

    // 상세 조회
    public LectureStuDTO getLectureDetail(Long lectureId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + lectureId));

        LectureStuDTO dto = modelMapper.map(entity, LectureStuDTO.class);

        // 👇 여기 숫자를 1L -> 2L 로 변경!
        // 2번 강사(이강사)의 강의들만 수강 중(true)인 것으로 테스트
        if (entity.getInstructorId() == 2L) {
            dto.setEnrolled(true);
        } else {
            dto.setEnrolled(false);
        }

        return dto;
    }
}