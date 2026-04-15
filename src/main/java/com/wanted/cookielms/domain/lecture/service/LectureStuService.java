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

    public LectureStuDTO getLectureDetail(Long lectureId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + lectureId));

        LectureStuDTO dto = modelMapper.map(entity, LectureStuDTO.class);

        if (entity.getInstructorId() == 2L) {
            dto.setEnrolled(true);
        } else {
            dto.setEnrolled(false);
        }

        return dto;
    }

    // 영상 URL 조회 (권한 체크 포함)
    public String getVideoUrl(Long lectureId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        // [임시 권한 체크] 나중에 팀원 코드가 오면 SecurityContext에서 유저를 꺼내 Enrollment 테이블을 검증합니다.
        // 이 로직이 추후 @PreAuthorize("hasRole('USER') and @enrollmentCheck.isEnrolled(#lectureId)") 같은 AOP로 대체됩니다.
        boolean isEnrolled = (entity.getInstructorId() == 2L);

        if (!isEnrolled) {
            throw new SecurityException("수강생만 강의 영상을 재생할 수 있습니다.");
        }

        return entity.getVideoUrl();
    }

    // 학습 자료 다운로드 (권한 체크 포함)
    public String getMaterialId(Long lectureId) {
        LectureStuEntity entity = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        // [임시 권한 체크] 이 부분 역시 나중에 AOP나 시큐리티로 교체됩니다.
        boolean isEnrolled = (entity.getInstructorId() == 2L);

        if (!isEnrolled) {
            throw new SecurityException("수강생만 학습 자료를 다운로드할 수 있습니다.");
        }

        if (entity.getMaterialId() == null || entity.getMaterialId().isEmpty()) {
            throw new IllegalArgumentException("등록된 학습 자료가 없습니다.");
        }

        return entity.getMaterialId();
    }
}