package com.wanted.cookielms.domain.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserBanDto {
    private Long id;
    private Long userId;
    private String reason;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
