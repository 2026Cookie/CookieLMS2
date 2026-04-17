package com.wanted.cookielms.domain.user.repository;

import com.wanted.cookielms.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 활성 계정 조회 (isDeleted = false 또는 null)
    @Query("SELECT u FROM User u WHERE u.loginId = :loginId AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    Optional<User> findByLoginIdAndIsDeletedFalse(@Param("loginId") String loginId);

    Optional<User> findByLoginId(String loginId);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.loginId = :loginId AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    boolean existsByLoginIdAndIsDeletedFalse(@Param("loginId") String loginId);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    boolean existsByEmailAndIsDeletedFalse(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.name = :name AND u.phone = :phone AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    Optional<User> findByNameAndPhoneAndIsDeletedFalse(@Param("name") String name, @Param("phone") String phone);

    @Query("SELECT u FROM User u WHERE u.loginId = :loginId AND u.name = :name AND u.phone = :phone AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    Optional<User> findByLoginIdAndNameAndPhoneAndIsDeletedFalse(@Param("loginId") String loginId, @Param("name") String name, @Param("phone") String phone);
}
