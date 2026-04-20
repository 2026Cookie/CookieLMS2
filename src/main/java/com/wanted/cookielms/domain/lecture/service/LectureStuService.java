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
@Transactional
//@Transactional(readOnly = true)
public class LectureStuService {

    private final LectureStuRepository lectureStuRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    // 전체 강의 목록 조회
    @BussinessServiceLogging
    public Page<MyLectureListDTO> getAllLectures(String keyword, Pageable pageable) {
        // 🚀 N+1 문제를 일으키던 findAll, findByTitleContaining, map 반복문을 싹 지우고
        // Repository에서 만든 '한 방 쿼리' 메서드를 바로 호출합니다.

        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword;

        // DB에서 이미 강사 이름까지 다 채워진 DTO를 받아오므로 후처리가 필요 없습니다!
        return lectureStuRepository.findLecturesWithInstructorName(safeKeyword, pageable);
    }

    // 내 강의만 가져오기
    public Page<MyLectureListDTO> getMyLectures(Long userId, String keyword, Pageable pageable) {
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword;
        return lectureStuRepository.findMyLecturesWithProjection(userId, safeKeyword, pageable);
    }

    public LectureStuDTO getLectureDetail(Long lectureId, Long userId) {
        // 🚀 새로 만든 '상세보기 한 방 쿼리'를 사용합니다!
        LectureStuDTO dto = lectureStuRepository.findLectureDetailWithInstructorName(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        // 권한 체크 (이건 그대로 유지)
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED");

        // 강사 본인인지 체크 (DB 다시 안 찌르고 DTO에 담긴 정보로 바로 비교 가능하게 필드 확인 필요)
        // 일단 에러가 났던 findInstructorNameById 호출 부분은 지우셔도 됩니다!

        dto.setUserEnrolled(isEnrolled);
        // dto.setUserInstructor(dto.getInstructorId().equals(userId)); // 필요시 추가

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
