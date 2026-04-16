package com.wanted.cookielms.domain.lecture.repository;

import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureInsRepository extends JpaRepository<InsLecture, Long> {

    Page<InsLecture> findByInstructorId(Long instructorId, Pageable pageable);

}