package com.wanted.cookielms.domain.auth.dto;

import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
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

    // 💡 아래 4개 메서드가 true를 반환해야 로그인이 허용됩니다.
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}