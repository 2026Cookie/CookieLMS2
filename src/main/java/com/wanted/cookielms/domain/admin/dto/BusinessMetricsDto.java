package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BusinessMetricsDto {
    private List<Map<String, Object>> slowMethods;
    private List<Map<String, Object>> failureMethods;
}