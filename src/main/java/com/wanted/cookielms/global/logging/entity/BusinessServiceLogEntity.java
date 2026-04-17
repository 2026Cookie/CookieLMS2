package com.wanted.cookielms.global.logging.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_service_logs", indexes = {
        @Index(name = "idx_class_method", columnList = "class_method"),
        @Index(name = "idx_is_success", columnList = "is_success"),
        @Index(name = "idx_trace_id", columnList = "trace_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 100)
    private String methodName;

    @Column(nullable = false, length = 255)
    private String classMethod;

    @Column(nullable = false)
    private Long executionTimeMs;

    @Column(nullable = false)
    private Boolean isSuccess;

    private Long userId;

    @Column(length = 36)
    private String traceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}