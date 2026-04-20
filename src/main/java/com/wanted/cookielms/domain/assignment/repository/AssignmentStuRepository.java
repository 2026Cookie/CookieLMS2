package com.wanted.cookielms.domain.assignment.repository;

import com.wanted.cookielms.domain.assignment.entity.AssignmentStuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentStuRepository extends JpaRepository<AssignmentStuEntity, Long> {
    // 나중에 특정 강의의 과제 목록을 불러올 때 사용할 메서드
    java.util.Optional<AssignmentStuEntity> findByLectureId(Long lectureId);
}
