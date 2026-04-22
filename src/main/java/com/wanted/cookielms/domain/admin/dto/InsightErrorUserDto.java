package com.wanted.cookielms.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InsightErrorUserDto {
    private Long userId;
    private String loginId;
    private Long errorCount;
    private LocalDateTime lastErrorTime;
}
