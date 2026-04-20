package com.wanted.cookielms.domain.assignment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder; // 🌟 빌더 임포트 추가
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignmentStuEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;

    // 🌟 데이터 생성을 위한 빌더 추가!
    @Builder
    public AssignmentStuEntity(String title, String content, LocalDateTime dueDate, Long lectureId) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.lectureId = lectureId;
    }
    public void updateAssignment(String title, String content, java.time.LocalDateTime dueDate) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
    }
}
