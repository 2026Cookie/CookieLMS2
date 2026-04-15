package com.wanted.cookielms.domain.enrollment.repository;

import com.wanted.cookielms.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);
}
