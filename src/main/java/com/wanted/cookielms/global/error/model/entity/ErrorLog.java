package com.wanted.cookielms.global.error.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs") // DB 테이블명 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성 시간 자동 기록
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String errorCode;      // 예: C001, CRS001

    @Column(nullable = false, length = 500)
    private String errorMessage;   // 예: 잘못된 입력값입니다.

    @Column(nullable = false, length = 100)
    private String exceptionName;  // 예: MethodArgumentNotValidException

    @Column(nullable = false, length = 500)
    private String requestUri;     // 예: /api/courses

    @Column(nullable = false, length = 10)
    private String httpMethod;     // 예: POST, GET

    @Column(length = 50)
    private String clientIp;       // 예: 192.168.0.1 (해킹 추적용)

    @Column(length = 50)
    private String userId;         // 예: admin@test.com (비로그인 시 ANONYMOUS)

    @Column(columnDefinition = "TEXT")
    private String stackTrace;     // 전체 에러 흔적 (매우 길 수 있으므로 TEXT 타입)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 에러 발생 시각

    @Builder
    public ErrorLog(String errorCode, String errorMessage, String exceptionName,
                    String requestUri, String httpMethod, String clientIp,
                    String userId, String stackTrace) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exceptionName = exceptionName;
        this.requestUri = requestUri;
        this.httpMethod = httpMethod;
        this.clientIp = clientIp;
        this.userId = userId;
        this.stackTrace = stackTrace;
    }
}