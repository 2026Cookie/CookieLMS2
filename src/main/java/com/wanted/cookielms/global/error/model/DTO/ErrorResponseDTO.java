package com.wanted.cookielms.global.error.model.DTO;

import com.wanted.cookielms.global.error.handler.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDTO {

    private int status;
    private String code;
    private String message;
    private String traceId;  // 요청 추적 ID

    @Builder
    private ErrorResponseDTO(int status, String code, String message, String traceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.traceId = traceId;
    }

    public static ErrorResponseDTO of(ErrorCode code, String traceId) {
        return ErrorResponseDTO.builder()
                .status(code.getStatus().value())
                .code(code.getCode())
                .message(code.getMessage())
                .traceId(traceId)
                .build();
    }
}