package com.wanted.cookielms.global.logging.api.entity;

import com.wanted.cookielms.domain.admin.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_performance_logs", indexes = {
        @Index(name = "idx_api_log_created_at", columnList = "created_at"),
        @Index(name = "idx_api_log_endpoint", columnList = "endpoint"),
        @Index(name = "idx_api_log_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPerformanceLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "http_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status_code")
    private Integer statusCode;
}