package com.wanted.cookielms.domain.admin.dto;

import com.wanted.cookielms.global.logging.businessService.dto.BusinessServiceLogResponseDto;
import com.wanted.cookielms.global.logging.error.dto.ErrorLogResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class TraceDetailDto {
    private String traceId;
    private List<BusinessServiceLogResponseDto> serviceLogs;
    private List<ErrorLogResponseDTO> errorLogs;
}