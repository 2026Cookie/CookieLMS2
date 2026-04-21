package com.wanted.cookielms.domain.enrollment.service;

import com.wanted.cookielms.domain.enrollment.entity.Enrollment;
import com.wanted.cookielms.domain.enrollment.entity.Waitlist;
import com.wanted.cookielms.domain.enrollment.exception.EnrollmentErrorCode;
import com.wanted.cookielms.domain.enrollment.exception.EnrollmentException;
import com.wanted.cookielms.domain.enrollment.repository.EnrollmentRepository;
import com.wanted.cookielms.domain.enrollment.repository.WaitlistRepository;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.repository.LectureStuRepository;
import com.wanted.cookielms.global.aop.BussinessServiceLogging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureStuRepository lectureStuRepository;

    @BussinessServiceLogging
    @Transactional
    public int registerWaitlist(Long lectureId, Long userId) {

        // 이미 대기 중인지 체크
        if (waitlistRepository.existsByUserIdAndLectureIdAndStatus(userId, lectureId, "WAITING")) {
            throw new EnrollmentException(EnrollmentErrorCode.ALREADY_WAITLISTED);
        }

        // 지금까지 등록된 최대 번호 + 1 = 새 대기 번호 (취소 후 재등록 시 중복 방지)
        int waitNumber = waitlistRepository.findMaxWaitNumberByLectureId(lectureId) + 1;

        Waitlist waitlist = Waitlist.builder()
                .userId(userId)
                .lectureId(lectureId)
                .waitNumber(waitNumber)
                .status("WAITING")
                .build();
        waitlistRepository.save(waitlist);

        return waitNumber;
    }

    public List<Long> getMyWaitingLectureIds(Long userId) {
        return waitlistRepository.findByUserIdAndStatus(userId, "WAITING")
                .stream()
                .map(Waitlist::getLectureId)
                .collect(Collectors.toList());
    }

    public int getWaitNumber(Long lectureId, Long userId) {
        Waitlist waitlist = waitlistRepository.findByUserIdAndLectureIdAndStatus(userId, lectureId, "WAITING")
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.WAITLIST_NOT_FOUND));

        // 나보다 waitNumber가 낮은 WAITING 인원 수 + 1 = 실제 현재 순위
        int ahead = waitlistRepository.countByLectureIdAndStatusAndWaitNumberLessThan(
                lectureId, "WAITING", waitlist.getWaitNumber());
        return ahead + 1;
    }

    @BussinessServiceLogging
    @Transactional
    public void cancelWaitlist(Long lectureId, Long userId) {
        Waitlist waitlist = waitlistRepository.findByUserIdAndLectureIdAndStatus(userId, lectureId, "WAITING")
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.WAITLIST_NOT_FOUND));
        waitlist.changeStatus("CANCELLED");
    }


    @Transactional
    public void autoEnroll(Long lectureId) {
        // 대기 번호 가장 낮은 1명 조회
        Optional<Waitlist> firstWaiting = waitlistRepository
                .findTopByLectureIdAndStatusOrderByWaitNumberAsc(lectureId, "WAITING");

        if (firstWaiting.isEmpty()) return;

        Waitlist waitlist = firstWaiting.get();

        // 수강 신청 자동 생성
        Enrollment enrollment = Enrollment.builder()
                .userId(waitlist.getUserId())
                .lectureId(lectureId)
                .status("ENROLLED")
                .build();
        enrollmentRepository.save(enrollment);

        // 강의 수강 인원 증가
        LectureStuEntity lecture = lectureStuRepository.findById(lectureId)
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.LECTURE_NOT_FOUND));
        lecture.increaseEnrollment();

        // 대기열 상태 변경
        waitlist.changeStatus("ENROLLED");
    }
}