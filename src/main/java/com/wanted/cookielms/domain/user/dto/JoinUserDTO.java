package com.wanted.cookielms.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class JoinUserDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min=4)
    private String loginId;
    @NotBlank
    @Pattern(regexp = "^(?=.*[!@#$%^&*]).{8,}$", message = "비밀번호는 8자 이상, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank
    private String passwordConfirm;
    @NotBlank
    private String phone;
}
