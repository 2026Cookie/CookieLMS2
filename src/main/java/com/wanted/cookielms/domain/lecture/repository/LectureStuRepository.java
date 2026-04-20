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

    // 1. [최적화] 전체 강의 목록 조회 (강사 이름 포함 + 최신순 정렬 적용)
    @Query("SELECT new com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO(" +
            "l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail) " +
            "FROM LectureStuEntity l " +
            "JOIN User u ON l.instructorId = u.userId " +
            "WHERE (:keyword IS NULL OR l.title LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY l.lectureId DESC")
    Page<MyLectureListDTO> findLecturesWithInstructorName(@Param("keyword") String keyword, Pageable pageable);

    // 2. [최적화] 내 강의 목록 조회 (내가 신청/완료한 강의만 필터링 + 최신순 정렬 적용)
    @Query("SELECT new com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO(" +
            "l.lectureId, l.title, u.name, l.currentEnrollment, l.maxCapacity, l.thumbnail) " +
            "FROM LectureStuEntity l " +
            "JOIN Enrollment e ON l.lectureId = e.lectureId " + // 1. 수강신청 테이블과 조인
            "JOIN User u ON l.instructorId = u.userId " +      // 2. 강사 정보 조인
            "WHERE e.userId = :userId " +                      // 🔥 핵심: 현재 로그인한 학생의 ID만 조회
            "AND e.status IN ('ENROLLED', 'COMPLETED') " +    // 🔥 수강 중이거나 완료된 강의 모두 포함
            "AND (:keyword IS NULL OR l.title LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY l.lectureId DESC")
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

    // 4. [기존 유지] 강사 ID로 이름만 쏙 빼오는 마법의 쿼리 (에러 방지용)
    @Query("SELECT u.name FROM User u WHERE u.userId = :instructorId")
    String findInstructorNameById(@Param("instructorId") Long instructorId);
}