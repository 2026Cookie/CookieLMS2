package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.exception.LectureErrorCode;
import com.wanted.cookielms.domain.lecture.exception.LectureException;
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

    // 🌟 전체 강의 목록 조회 (수강신청 페이지용 복구)
    public Page<LectureStuDTO> getAllLectures(String keyword, Pageable pageable) {
        Page<LectureStuEntity> lectures;
        if (keyword == null || keyword.trim().isEmpty()) {
            lectures = lectureStuRepository.findAll(pageable);
        } else {
            lectures = lectureStuRepository.findByTitleContaining(keyword, pageable);
        }
        return lectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }

    // 🌟 내 강의만 가져오는 최적화된 로직
    public Page<MyLectureListDTO> getMyLectures(Long userId, String keyword, Pageable pageable) {
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword;
        return lectureStuRepository.findMyLecturesWithProjection(userId, safeKeyword, pageable);
    }

    // 🌟 강의 상세 조회 (여기에 강사 이름 가져오기가 쏙 들어갔어요!)
    public LectureStuDTO getLectureDetail(Long lectureId, Long userId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        LectureStuDTO dto = modelMapper.map(entity, LectureStuDTO.class);

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        // 🌟 강사 이름 가져와서 DTO에 꽂아주기! (하연님이 물어보신 부분!)
        String instructorName = lectureStuRepository.findInstructorNameById(entity.getInstructorId());
        dto.setInstructorName(instructorName);

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
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        if (!isEnrolled && !isInstructor) {
            throw new LectureException(LectureErrorCode.VIDEO_ACCESS_DENIED);
        }
        return entity.getVideoUrl();
    }

    public String getMaterialId(Long lectureId, Long userId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");
        boolean isInstructor = entity.getInstructorId().equals(userId);

        if (!isEnrolled && !isInstructor) {
            throw new LectureException(LectureErrorCode.MATERIAL_ACCESS_DENIED);
        }
        return entity.getMaterialId();
    }
}