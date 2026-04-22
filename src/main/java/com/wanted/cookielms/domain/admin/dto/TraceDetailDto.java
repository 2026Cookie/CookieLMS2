package com.wanted.cookielms.domain.admin.dto;

import com.wanted.cookielms.global.logging.businessService.dto.BusinessServiceLogResponseDto;
import com.wanted.cookielms.global.logging.error.dto.ErrorLogResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraceDetailDto {
    private String traceId;
    private ApiPerformanceLogDto apiLog;
    private List<BusinessServiceLogResponseDto> serviceLogs;
    private List<ErrorLogResponseDTO> errorLogs;

    public TraceDetailDto(String traceId, List<BusinessServiceLogResponseDto> serviceLogs, List<ErrorLogResponseDTO> errorLogs) {
        this.traceId = traceId;
        this.serviceLogs = serviceLogs;
        this.errorLogs = errorLogs;
    }
}