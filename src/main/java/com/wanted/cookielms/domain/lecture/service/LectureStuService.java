package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.exception.LectureErrorCode;
import com.wanted.cookielms.domain.lecture.exception.LectureException;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import com.wanted.cookielms.global.aop.BussinessServiceLogging;
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
    @BussinessServiceLogging
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

        // 🌟 DB에서 꺼낸 원래 주소를 퍼가기용(Embed) 주소로 변환해서 반환!
        return convertToEmbedUrl(entity.getVideoUrl());
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

    // 유튜브 주소 변환기 메서드
    private String convertToEmbedUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        String videoId = "";

        // 케이스 1: https://youtu.be/영상ID 형태
        if (url.contains("youtu.be/")) {
            videoId = url.substring(url.indexOf("youtu.be/") + 9);
        }
        // 케이스 2: https://www.youtube.com/watch?v=영상ID 형태
        else if (url.contains("watch?v=")) {
            int vIndex = url.indexOf("v=") + 2;
            int ampersandIndex = url.indexOf("&", vIndex); // &t=12s 같은 추가 파라미터 방지
            videoId = ampersandIndex != -1 ? url.substring(vIndex, ampersandIndex) : url.substring(vIndex);
        }
        // 케이스 3: 이미 강사님이 똑똑하게 임베드 링크를 넣은 경우
        else if (url.contains("/embed/")) {
            return url;
        }

        // 비디오 ID를 성공적으로 뽑아냈다면 임베드 주소로 조립!
        if (!videoId.isEmpty()) {
            return "https://www.youtube.com/embed/" + videoId;
        }

        // 변환할 수 없는 이상한 주소라면 일단 그대로 반환
        return url;
    }
}
