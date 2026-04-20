package com.wanted.cookielms.domain.enrollment.service;

import com.wanted.cookielms.domain.enrollment.entity.Enrollment;
import com.wanted.cookielms.domain.enrollment.exception.EnrollmentErrorCode;
import com.wanted.cookielms.domain.enrollment.exception.EnrollmentException;
import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import com.wanted.cookielms.global.aop.BussinessServiceLogging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureStuRepository lectureStuRepository;
    private final WaitlistService waitlistService;

    @BussinessServiceLogging
    @Transactional
    public void enroll(Long lectureId, Long userId) {

        // 1. 중복 수강 신청 체크
        if (enrollmentRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED")) {
            throw new EnrollmentException(EnrollmentErrorCode.ALREADY_ENROLLED);
        }

        // 2. 강의 조회
        LectureStuEntity lecture = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.LECTURE_NOT_FOUND));

        // 3. 정원 초과 체크
        if (lecture.getCurrentEnrollment() >= lecture.getMaxCapacity()) {
            throw new EnrollmentException(EnrollmentErrorCode.ENROLLMENT_CAPACITY_EXCEEDED);
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

    @Transactional
    public void cancel(Long lectureId, Long userId) {

        // 1. 수강 신청 내역 조회
        Enrollment enrollment = enrollmentRepository.findByUserIdAndLectureIdAndStatus(userId, lectureId, "ENROLLED")
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND));

        // 2. 수강 취소 처리
        enrollment.changeStatus("CANCELLED");

        // 3. 강의 수강 인원 감소
        LectureStuEntity lecture = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.LECTURE_NOT_FOUND));
        lecture.decreaseEnrollment();

        // 4. 대기열 1번 자동 수강 신청
        waitlistService.autoEnroll(lectureId);
    }

    public List<Long> getMyEnrolledLectureIds(Long userId) {
        return enrollmentRepository.findByUserIdAndStatus(userId, "ENROLLED")
                .stream()
                .map(Enrollment::getLectureId)
                .collect(Collectors.toList());
    }
}