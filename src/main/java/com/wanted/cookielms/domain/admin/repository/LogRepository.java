package com.wanted.cookielms.domain.admin.repository;

import com.wanted.cookielms.domain.admin.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
