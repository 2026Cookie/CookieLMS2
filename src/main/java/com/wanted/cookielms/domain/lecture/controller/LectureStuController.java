package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureStuService;

    @GetMapping
    public ResponseEntity<Page<LectureStuDTO>> getLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureStuDTO> getLectureDetail(@PathVariable Long lectureId) {
        LectureStuDTO detail = lectureStuService.getLectureDetail(lectureId);
        return ResponseEntity.ok(detail);
    }
}