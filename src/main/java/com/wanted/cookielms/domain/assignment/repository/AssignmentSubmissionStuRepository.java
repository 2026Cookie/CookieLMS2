package com.wanted.cookielms.domain.assignment.repository;

import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionStuRepository extends JpaRepository<AssignmentSubmissionStuEntity, Long> {
    // 🌟 추가: 해당 과제에 이 학생이 제출한 내역이 있는지 조회
    Optional<AssignmentSubmissionStuEntity> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}