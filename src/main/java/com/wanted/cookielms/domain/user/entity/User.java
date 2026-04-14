package com.wanted.cookielms.domain.user.entity;

import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String id;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deletedAt;
    private Boolean isDeleted;
    private Role role;
    private Status status;

}
