package com.wanted.cookielms.global.logging.error.entity;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs", indexes = {
        @Index(name = "idx_trace_id", columnList = "traceId")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ErrorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String errorCode;

    @Column(nullable = false, length = 500)
    private String errorMessage;

    @Column(nullable = false, length = 100)
    private String exceptionName;

    @Column(length = 50)
    private String clientIp;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(length = 36)
    private String traceId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ErrorSeverity severity;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ErrorLogEntity(String errorCode, String errorMessage, String exceptionName,
                          String clientIp, String stackTrace, String traceId, ErrorSeverity severity) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exceptionName = exceptionName;
        this.clientIp = clientIp;
        this.stackTrace = stackTrace;
        this.traceId = traceId;
        this.severity = severity;
    }
}