package com.wanted.cookielms.domain.assignment.repository;

import com.wanted.cookielms.domain.assignment.entity.AssignmentSubmissionStuEntity;
import com.wanted.cookielms.domain.assignment.dto.AssignmentStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionStuRepository extends JpaRepository<AssignmentSubmissionStuEntity, Long> {
    // 해당 과제에 이 학생이 제출한 내역이 있는지 조회
    Optional<AssignmentSubmissionStuEntity> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    @Query("SELECT new com.wanted.cookielms.domain.assignment.dto.AssignmentStatusDTO(" +
            "u.userId, u.name, u.email, u.phone, s.submittedAt, s.id) " +
            "FROM Enrollment e " +
            "JOIN User u ON e.userId = u.userId " +
            "LEFT JOIN AssignmentSubmissionStuEntity s ON s.studentId = u.userId AND s.assignmentId = :assignmentId " +
            "WHERE e.lectureId = :lectureId")
    List<AssignmentStatusDTO> findSubmissionStatusByAssignmentAndLecture(
            @Param("assignmentId") Long assignmentId,
            @Param("lectureId") Long lectureId);
}