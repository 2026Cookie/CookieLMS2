package com.wanted.cookielms.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MypageDTO {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private String nickname;
    private String phone;
}
