package com.wanted.cookielms.domain.enrollment.repository;

import com.wanted.cookielms.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndLectureIdAndStatus(Long userId, Long lectureId, String status);

    Optional<Enrollment> findByUserIdAndLectureIdAndStatus(Long userId, Long lectureId, String status);

    List<Enrollment> findByUserIdAndStatus(Long userId, String status);
}
