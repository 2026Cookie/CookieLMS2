package com.wanted.cookielms.domain.enrollment.repository;

import com.wanted.cookielms.domain.enrollment.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    boolean existsByUserIdAndLectureIdAndStatus(Long userId, Long lectureId, String status);

    int countByLectureIdAndStatus(Long lectureId, String status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(w.waitNumber), 0) FROM Waitlist w WHERE w.lectureId = :lectureId")
    int findMaxWaitNumberByLectureId(@org.springframework.data.repository.query.Param("lectureId") Long lectureId);

    Optional<Waitlist> findTopByLectureIdAndStatusOrderByWaitNumberAsc(Long lectureId, String status);

    Optional<Waitlist> findByUserIdAndLectureIdAndStatus(Long userId, Long lectureId, String status);

    java.util.List<Waitlist> findByUserIdAndStatus(Long userId, String status);

    int countByLectureIdAndStatusAndWaitNumberLessThan(Long lectureId, String status, int waitNumber);
}
