package com.wanted.cookielms.domain.admin.repository;

import com.wanted.cookielms.domain.admin.entity.UserBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBanRepository extends JpaRepository<UserBan, Long> {

}
