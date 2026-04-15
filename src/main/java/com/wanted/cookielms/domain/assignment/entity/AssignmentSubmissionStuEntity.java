package com.wanted.cookielms.domain.assignment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignmentSubmissionStuEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "score")
    private Long score;

    @Column(name = "feedback")
    private String feedback;

    @Builder
    public AssignmentSubmissionStuEntity(Long assignmentId, Long studentId, Long fileId) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.fileId = fileId;
    }

    // 덮어쓰기 메서드에 시간 갱신 로직 추가!
    public void updateFileId(Long newFileId) {
        this.fileId = newFileId;
        this.submittedAt = LocalDateTime.now(); // 재제출한 현재 시간으로 덮어쓰기!
    }
}