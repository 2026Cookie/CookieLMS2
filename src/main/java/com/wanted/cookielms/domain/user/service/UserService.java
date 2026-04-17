package com.wanted.cookielms.domain.user.service;

import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.dto.ModifyUserInfo;
import com.wanted.cookielms.domain.user.dto.ResetPasswordDTO;
import com.wanted.cookielms.domain.user.entity.User;
import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import com.wanted.cookielms.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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


        User user = new User(
                joinUserDTO.getEmail(),
                joinUserDTO.getLoginId(),
                passwordEncoder.encode(joinUserDTO.getPassword()),
                joinUserDTO.getName(),
                joinUserDTO.getNickname(),
                joinUserDTO.getPhone(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                joinUserDTO.getRole(),
                Status.ACTIVE
        );
        userRepository.save(user); // 맨 마지막에!
        return "success";


    }

    public LoginUserDTO findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByLoginId(username);
        return userOptional.map(user -> modelMapper.map(user, LoginUserDTO.class)).orElse(null);
    }

    public String findLoginIdByNameAndPhone(String name, String phone) {
        Optional<User> lostIdUser = userRepository.findByNameAndPhone(name, phone);
        return lostIdUser.map(User -> User.getLoginId()).orElse(null);
    }

    public Boolean findByLoginIdAndNameAndPhone(String loginId, String name, String phone) {
        Optional<User> lostpwdUser = userRepository.findByLoginIdAndNameAndPhone(loginId, name, phone);
        return lostpwdUser.isPresent();
    }

    @Transactional
    public void updatePassword(String loginId, String newPassword ) {
        userRepository.findByLoginId(loginId)
                .ifPresent(user -> {user.updatePassword(passwordEncoder.encode(newPassword));
                });

    }

    // 정보 조회 비번 확인용
    public boolean verifyPassword(String loginId, String password) {
        return userRepository.findByLoginId(loginId)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }


    @Transactional
    public boolean updateUserInfo(String loginId, ModifyUserInfo dto) {
        User user = userRepository.findByLoginId(loginId).orElse(null);
        if (user == null) return false;

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        user.updateInfo(dto.getName(), dto.getNickname(), dto.getEmail(), dto.getPhone());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
                return false;
            }
            user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        return true;
    }

}
