package com.wanted.cookielms.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserInfo {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    private String currentPassword;

    @Pattern(regexp = "^$|^(?=.*[!@#$%^&*]).{8,}$", message = "비밀번호는 특수문자를 포함한 8자 이상이어야 합니다.")
    private String newPassword;

    private String newPasswordConfirm;
}
