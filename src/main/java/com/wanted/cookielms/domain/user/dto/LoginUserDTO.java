package com.wanted.cookielms.domain.user.dto;

import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
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
    private String nickname;
    private Role role;
    private Status status;
    private Boolean isDeleted;

}
