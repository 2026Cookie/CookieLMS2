package com.wanted.cookielms.domain.lecture.repository;

import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureStuRepository extends JpaRepository<LectureStuEntity, Long> {

    // 1. [최적화] 전체 강의 목록 조회 (강사 이름 포함)
    @Query("SELECT new com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO(" +
            "l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail) " +
            "FROM LectureStuEntity l " +
            "JOIN User u ON l.instructorId = u.userId " +
            "WHERE (:keyword IS NULL OR l.title LIKE CONCAT('%', :keyword, '%'))")
    Page<MyLectureListDTO> findLecturesWithInstructorName(@Param("keyword") String keyword, Pageable pageable);

    // 2. [최적화] 내 강의 목록 조회 (수강 신청한 강의만)
    @Query("SELECT new com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO(" +
            "l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail) " +
            "FROM LectureStuEntity l " +
            "JOIN Enrollment e ON l.lectureId = e.lectureId " +
            "JOIN User u ON l.instructorId = u.userId " +
            "WHERE e.userId = :userId AND e.status = 'ENROLLED' " +
            "AND (:keyword IS NULL OR l.title LIKE CONCAT('%', :keyword, '%'))")
    Page<MyLectureListDTO> findMyLecturesWithProjection(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 3. [최적화] 강의 상세 조회 (강사 이름 포함 한 방 쿼리)
    @Query("SELECT new com.wanted.cookielms.domain.lecture.dto.LectureStuDTO(" +
            "l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail, l.videoUrl, l.materialId) " +
            "FROM LectureStuEntity l " +
            "JOIN User u ON l.instructorId = u.userId " +
            "WHERE l.lectureId = :lectureId")
    Optional<LectureStuDTO> findLectureDetailWithInstructorName(@Param("lectureId") Long lectureId);
}