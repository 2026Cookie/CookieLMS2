package com.wanted.cookielms.domain.user.repository;

import com.wanted.cookielms.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);


    boolean existsByLoginId(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByNameAndPhone(String name, String phone);

    Optional<User> findByLoginIdAndNameAndPhone(String loginId, String name, String phone);
}
