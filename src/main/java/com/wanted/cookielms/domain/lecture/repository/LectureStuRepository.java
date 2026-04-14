package com.wanted.cookielms.domain.lecture.repository;

import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureStuRepository extends JpaRepository<LectureStuEntity, Long> {

    // 강의 제목에 'keyword'가 포함된 것만 찾아오는 메서드
    Page<LectureStuEntity> findByTitleContaining(String keyword, Pageable pageable);
}