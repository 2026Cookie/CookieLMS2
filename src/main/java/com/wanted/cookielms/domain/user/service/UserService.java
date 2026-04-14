package com.wanted.cookielms.domain.user.service;

import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.entity.User;
import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import com.wanted.cookielms.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Join;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    public String join(JoinUserDTO joinUserDTO){

        if (userRepository.existsByLoginId(joinUserDTO.getLoginId())) {
            return null; // 중복된 아이디가 존재함을 null로 표시
        }

        if (userRepository.existsByEmail(joinUserDTO.getEmail())) {
            return null;
        }


        User user = new User();
        user.setName(joinUserDTO.getName());
        user.setLoginId(joinUserDTO.getLoginId());
        user.setPassword(passwordEncoder.encode(joinUserDTO.getPassword()));
        user.setEmail(joinUserDTO.getEmail());
        user.setNickname(joinUserDTO.getNickname());
        user.setPhone(joinUserDTO.getPhone());
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user); // 맨 마지막에!
        return "success";


    }

}
