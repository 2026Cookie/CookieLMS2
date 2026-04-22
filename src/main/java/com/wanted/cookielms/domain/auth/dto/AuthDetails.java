package com.wanted.cookielms.domain.auth.dto;

import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.enums.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AuthDetails implements UserDetails {
    private LoginUserDTO loginUserDTO;

    public AuthDetails() {}

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
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + loginUserDTO.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return loginUserDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return loginUserDTO.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return !Status.BANNED.equals(loginUserDTO.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return !Boolean.TRUE.equals(loginUserDTO.getIsDeleted());
    }
}