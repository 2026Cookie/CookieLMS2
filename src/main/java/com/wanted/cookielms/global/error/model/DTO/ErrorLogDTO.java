package com.wanted.cookielms.global.error.model.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorLogDTO {

    private Long id;
    private String errorCode;
    private String errorMessage;
    private String exceptionName;
    private String requestUri;
    private String httpMethod;
    private String clientIp;
    private String userId;
    private LocalDateTime createdAt;
    // 목록 조회 시에는 stackTrace를 제외하여 응답 용량을 줄입니다.


}