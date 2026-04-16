package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;
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

    // 학생용 상세 조회 (수강생/강사 여부 체크 포함)
    public LectureStuDTO getLectureDetail(Long lectureId, Long userId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + lectureId));

        LectureStuDTO dto = modelMapper.map(entity, LectureStuDTO.class);

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        // 🌟 바뀐 이름으로 Setter 호출!
        dto.setUserEnrolled(isEnrolled);
        dto.setUserInstructor(isInstructor);

        return dto;
    }

    // 강사 컨트롤러 에러 방지를 위한 오버로딩 메서드
    public LectureStuDTO getLectureDetail(Long lectureId) {
        return getLectureDetail(lectureId, -1L);
    }

    public String getVideoUrl(Long lectureId, Long userId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        if (!isEnrolled && !isInstructor) {
            throw new SecurityException("수강생 또는 담당 강사만 강의 영상을 재생할 수 있습니다.");
        }
        return entity.getVideoUrl();
    }

    public String getMaterialId(Long lectureId, Long userId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        if (!isEnrolled && !isInstructor) {
            throw new SecurityException("수강생 또는 담당 강사만 학습 자료를 다운로드할 수 있습니다.");
        }
        return entity.getMaterialId();
    }
}