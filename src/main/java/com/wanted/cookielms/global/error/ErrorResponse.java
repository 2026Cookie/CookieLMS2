package com.wanted.cookielms.global.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
    private String traceId;  // 요청 추적 ID

    @Builder
    private ErrorResponse(int status, String code, String message, String traceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.traceId = traceId;
    }

    public static ErrorResponse of(ErrorCode code, String traceId) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .code(code.getCode())
                .message(code.getMessage())
                .traceId(traceId)
                .build();
    }
}