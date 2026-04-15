package com.wanted.cookielms.domain.auth.service;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {


    private final UserService userService;

    /**
     * AuthenticationProvider에서 호출하는 메서드로
     * login 요청 시 전달된 사용자의 id를 매개변수로 DB에서 사용자의 정보를 찾는다.
     * 전달된 사용자의 개체 타입은 UserDetails를 구현한 구현체가 되어야 한다.
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDTO login = userService.findByUsername(username);

        if(Objects.isNull(login)){
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다.");
        }

        return new AuthDetails(login);
    }
}