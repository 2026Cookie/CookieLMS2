package com.wanted.cookielms.domain.enrollment.service;

import com.wanted.cookielms.domain.enrollment.entity.Enrollment;
import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureStuRepository lectureStuRepository;

    @Transactional
    public void enroll(Long lectureId, Long userId) {

        // 1. 중복 수강 신청 체크
        if (enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new IllegalStateException("이미 수강 신청한 강의입니다.");
        }

        // 2. 강의 조회
        LectureStuEntity lecture = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다. ID: " + lectureId));

        // 3. 정원 초과 체크
        if (lecture.getCurrentEnrollment() >= lecture.getMaxCapacity()) {
            throw new IllegalStateException("수강 정원이 초과되었습니다.");
        }

        // 4. 수강 신청 저장
        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .lectureId(lectureId)
                .status("ENROLLED")
                .build();
        enrollmentRepository.save(enrollment);

        // 5. 현재 수강 인원 증가
        lecture.increaseEnrollment();
    }
}
