package com.wanted.cookielms.domain.lecture.repository;

import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.entity.LectureStuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureStuRepository extends JpaRepository<LectureStuEntity, Long> {

    // 수강신청 페이지용 전체 강의 검색 메서드 복구!
    Page<LectureStuEntity> findByTitleContaining(String keyword, Pageable pageable);

    // 내 강의 전용 성능 최적화(DTO Projection)
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

    // 강사 ID로 강사 이름(name)만 쏙 빼오는 쿼리 (여기에 추가!)
    @Query("SELECT u.name FROM User u WHERE u.userId = :instructorId")
    String findInstructorNameById(@Param("instructorId") Long instructorId);

}