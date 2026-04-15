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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    // eum 클래는 필드 순서를 따라 번호가 부여된다.
    // ordinal , string
    // userDetails 는 앤티티에 있으면 안된다, 수정 반드시 필요!!
    private Role role;
    private Status status;
}
