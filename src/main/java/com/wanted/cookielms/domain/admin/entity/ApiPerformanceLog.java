package com.wanted.cookielms.domain.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.wanted.cookielms.domain.admin.enums.HttpMethod;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_performance_logs", indexes = {
        @Index(name = "idx_api_log_created_at", columnList = "created_at"),
        @Index(name = "idx_api_log_endpoint", columnList = "endpoint")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPerformanceLog {

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
}