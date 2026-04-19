package com.wanted.cookielms.domain.admin.dto;

import com.wanted.cookielms.domain.user.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserDto {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private Status status;
}
