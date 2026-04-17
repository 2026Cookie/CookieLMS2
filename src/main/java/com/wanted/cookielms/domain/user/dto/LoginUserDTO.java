package com.wanted.cookielms.domain.user.dto;

import com.wanted.cookielms.domain.user.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginUserDTO {

    private Long userId;
    private String loginId;
    private String password;
    private String name;
    private Role role;

    // 마이페이지용 필드 추가
    private String email;
    private String nickname;
    private String phone;



    private Boolean isDeleted;

}
