package com.wanted.cookielms.domain.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.wanted.cookielms.domain.admin.enums.HttpMethod;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_performance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPerformanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "endpoint")
    private String endpoint;

    //get,post
    @Column(name = "http_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
