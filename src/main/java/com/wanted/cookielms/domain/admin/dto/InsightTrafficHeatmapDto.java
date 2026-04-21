package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InsightTrafficHeatmapDto {
    private Integer dayOfWeek;
    private Integer hour;
    private Long callCount;
    private String dayName;
}
