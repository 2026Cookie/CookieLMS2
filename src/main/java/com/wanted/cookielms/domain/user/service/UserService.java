package com.wanted.cookielms.domain.user.service;

import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.dto.ModifyUserInfo;
import com.wanted.cookielms.domain.user.dto.MypageDTO;
import com.wanted.cookielms.domain.user.dto.ResetPasswordDTO;
import com.wanted.cookielms.domain.user.entity.User;
import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import com.wanted.cookielms.domain.user.exception.UserErrorCode;
import com.wanted.cookielms.domain.user.exception.UserException;
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

        if (userRepository.existsByLoginIdAndIsDeletedFalse(joinUserDTO.getLoginId())) {
            throw new UserException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(joinUserDTO.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
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
        userRepository.save(user);
        return "success";
    }

    public LoginUserDTO findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByLoginIdAndIsDeletedFalse(username);
        return userOptional.map(user -> modelMapper.map(user, LoginUserDTO.class)).orElse(null);
    }

    public MypageDTO getMypage(String loginId) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return modelMapper.map(user, MypageDTO.class);
    }

    public String findLoginIdByNameAndPhone(String name, String phone) {
        Optional<User> lostIdUser = userRepository.findByNameAndPhoneAndIsDeletedFalse(name, phone);
        return lostIdUser.map(User::getLoginId).orElse(null);
    }

    public Boolean findByLoginIdAndNameAndPhone(String loginId, String name, String phone) {
        Optional<User> lostpwdUser = userRepository.findByLoginIdAndNameAndPhoneAndIsDeletedFalse(loginId, name, phone);
        return lostpwdUser.isPresent();
    }

    @Transactional
    public void updatePassword(String loginId, String newPassword ) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    // 정보 조회 비번 확인용
    public boolean verifyPassword(String loginId, String password) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Transactional
    public boolean updateUserInfo(String loginId, ModifyUserInfo dto) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        user.updateInfo(dto.getName(), dto.getNickname(), dto.getEmail(), dto.getPhone());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
                throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
            }
            user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        return true;
    }

    @Transactional
    public boolean withdrawUser(String loginId, String password) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        user.withdraw();
        return true;
    }

}
