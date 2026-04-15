package com.wanted.cookielms.domain.auth.dto;

import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthDetails implements UserDetails {
    private LoginUserDTO loginUserDTO;

    public AuthDetails(){
    }

    public AuthDetails(LoginUserDTO loginUserDTO) {
        this.loginUserDTO = loginUserDTO;
    }

    public LoginUserDTO getLoginUserDTO() {
        return loginUserDTO;
    }

    public void setLoginUserDTO(LoginUserDTO loginUserDTO) {
        this.loginUserDTO = loginUserDTO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + loginUserDTO.getRole().name());
    }

    @Override
    public String getPassword() {
        return loginUserDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return loginUserDTO.getLoginId();
    }
}
