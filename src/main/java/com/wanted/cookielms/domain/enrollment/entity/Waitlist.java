package com.wanted.cookielms.domain.enrollment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Long waitlistId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;

    @Column(name = "wait_number", nullable = false)
    private Integer waitNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 20, nullable = false)
    private String status; // "WAITING", "ENROLLED", "CANCELLED"

    @Builder
    private Waitlist(Long userId, Long lectureId, Integer waitNumber, String status) {
        this.userId = userId;
        this.lectureId = lectureId;
        this.waitNumber = waitNumber;
        this.status = status;
    }

    public void changeStatus(String status) {
        this.status = status;
    }

    // [성능 비교용] 재정렬 방식에서 사용
    public void decreaseWaitNumber() {
        this.waitNumber--;
    }
}
