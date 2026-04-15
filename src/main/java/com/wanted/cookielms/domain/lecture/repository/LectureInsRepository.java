package com.wanted.cookielms.domain.lecture.repository;

import com.wanted.cookielms.domain.lecture.entity.InsLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureInsRepository extends JpaRepository<InsLecture, Long>{

    }