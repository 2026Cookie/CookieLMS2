package com.wanted.cookielms.domain.admin.dto;

import lombok.*;
import com.wanted.cookielms.domain.admin.enums.ActionType;
import com.wanted.cookielms.domain.admin.enums.Severity;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserLogDto {
    private Long logId;
    private ActionType actionType;
    private Severity severity;
    private String ipAddress;
    private LocalDateTime createdAt;
    private Long userId;
}
