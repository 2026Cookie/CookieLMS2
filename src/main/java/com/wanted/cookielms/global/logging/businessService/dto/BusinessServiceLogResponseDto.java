package com.wanted.cookielms.global.logging.businessService.dto;

import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class BusinessServiceLogResponseDto {
    private final Long id;
    private final String classMethod;
    private final Long executionTimeMs;
    private final Boolean isSuccess;
    private final String traceId;
    private final LocalDateTime createdAt;

    private BusinessServiceLogResponseDto(BusinessServiceLogEntity entity) {
        this.id = entity.getId();
        this.classMethod = entity.getClassMethod();
        this.executionTimeMs = entity.getExecutionTimeMs();
        this.isSuccess = entity.getIsSuccess();
        this.traceId = entity.getTraceId();
        this.createdAt = entity.getCreatedAt();
    }

    public static BusinessServiceLogResponseDto from(BusinessServiceLogEntity entity) {
        return new BusinessServiceLogResponseDto(entity);
    }
}