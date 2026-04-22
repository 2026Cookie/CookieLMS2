package com.wanted.cookielms.domain.admin.service;

import com.wanted.cookielms.domain.admin.dto.AdminUserDto;
import com.wanted.cookielms.domain.user.entity.User;
import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import com.wanted.cookielms.domain.user.exception.UserErrorCode;
import com.wanted.cookielms.domain.user.exception.UserException;
import com.wanted.cookielms.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<AdminUserDto> getUserList() {
        return userRepository.findAllByIsDeletedFalseAndRole(Role.USER).stream()
                .map(u -> new AdminUserDto(u.getUserId(), u.getLoginId(), u.getName(), u.getEmail(), u.getStatus(), false))
                .collect(Collectors.toList());
    }

    public List<AdminUserDto> getBannedUserList() {
        return userRepository.findAllByIsDeletedFalseAndRole(Role.USER).stream()
                .filter(u -> u.getStatus() == Status.BANNED)
                .map(u -> new AdminUserDto(u.getUserId(), u.getLoginId(), u.getName(), u.getEmail(), u.getStatus(), false))
                .collect(Collectors.toList());
    }

    public List<AdminUserDto> getWithdrawnUserList() {
        return userRepository.findAllByIsDeletedTrueAndRole(Role.USER).stream()
                .map(u -> new AdminUserDto(u.getUserId(), u.getLoginId(), u.getName(), u.getEmail(), u.getStatus(), true))
                .collect(Collectors.toList());
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.ban();
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.unban();
    }
}
