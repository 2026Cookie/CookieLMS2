package com.wanted.cookielms.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserInfo {
    private String name;
    private String nickname;

    @Email
    private String email;

    private String phone;
    private String currentPassword;

    @Pattern(regexp = "^(?=.*[!@#$%^&*]).{8,}$", message = "비밀번호는 특수문자를 포함한 8자 이상이어야 합니다.")
    private String newPassword;

    private String newPasswordConfirm;
}
