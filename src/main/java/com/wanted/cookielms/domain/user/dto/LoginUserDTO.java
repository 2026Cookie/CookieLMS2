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

    private String loginId;
    private String password;
    private String name;
    private Role role;

}
