package com.wanted.cookielms.domain.admin.repository;

import com.wanted.cookielms.domain.admin.entity.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, Long> {
}
