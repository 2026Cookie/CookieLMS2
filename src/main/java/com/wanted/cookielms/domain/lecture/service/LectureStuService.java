package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.domain.assignment.repository.AssignmentStuRepository; // 🌟 하연님의 과제 로직
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
@Transactional
public class LectureStuService {

    private final LectureStuRepository lectureStuRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    // 과제 저장소 주입
    private final AssignmentStuRepository assignmentStuRepository;

    // 전체 강의 목록 조회
    @BussinessServiceLogging
    public Page<MyLectureListDTO> getAllLectures(String keyword, Pageable pageable) {
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword;
        return lectureStuRepository.findLecturesWithInstructorName(safeKeyword, pageable);
    }

    // 내 강의만 가져오기
    public Page<MyLectureListDTO> getMyLectures(Long userId, String keyword, Pageable pageable) {
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword;
        return lectureStuRepository.findMyLecturesWithProjection(userId, safeKeyword, pageable);
    }

    // 강의 상세 조회
    public LectureStuDTO getLectureDetail(Long lectureId, Long userId) {
        // 🚀 팀원 코드: 새로 만든 '상세보기 한 방 쿼리'를 사용해서 DTO를 바로 가져옵니다.
        LectureStuDTO dto = lectureStuRepository.findLectureDetailWithInstructorName(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        // 권한 체크 로직
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");

        // 강사 본인인지 체크 (DTO에 담겨온 instructorId를 사용해서 비교)
        boolean isInstructor = dto.getInstructorId() != null && dto.getInstructorId().equals(userId);

        dto.setUserEnrolled(isEnrolled);
        dto.setUserInstructor(isInstructor);

        // 이 강의에 연결된 과제가 있다면 그 과제의 ID를 DTO에 담아줍니다!
        assignmentStuRepository.findByLectureId(lectureId)
                .ifPresent(assignment -> dto.setAssignmentId(assignment.getId()));

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

        // DB에서 꺼낸 원래 주소를 퍼가기용(Embed) 주소로 변환해서 반환!
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
            int ampersandIndex = url.indexOf("&", vIndex);
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

        return url;
    }
}