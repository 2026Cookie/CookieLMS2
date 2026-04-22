package com.wanted.cookielms.domain.user.entity;

import com.wanted.cookielms.domain.user.enums.Role;
import com.wanted.cookielms.domain.user.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id")
    private Long userId;
    private String email;

    @Column(name = "id")
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    private Boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    public User(String email, String loginId, String password,
                String name, String nickname, String phone,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                Role role, Status status) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.status = status;
        this.isDeleted = false;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.updatedAt = LocalDateTime.now();
    }


    public void updateInfo(String name, String nickname, String email, String phone) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.status = Status.DORMANT;
    }

    public void ban() {
        this.status = Status.BANNED;
        this.updatedAt = LocalDateTime.now();
    }

    public void unban() {
        this.status = Status.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}
