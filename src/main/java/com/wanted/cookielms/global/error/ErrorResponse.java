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

    @Builder
    private ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode code) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .code(code.getCode())
                .message(code.getMessage())
                .build();
    }
}