package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureStuService;

    private Long getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return -1L;
        }
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return authDetails.getLoginUserDTO().getUserId();
    }

    // 🌟 전체 강의 조회 (수강신청 페이지용)
    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<LectureStuDTO>> getLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
    }

    // 🌟 내 강의 조회 (내 강의 목록 페이지용)
    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<Page<MyLectureListDTO>> getMyLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable,
            Authentication authentication) {

        Long userId = getLoginUserId(authentication);
        if (userId == -1L) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(lectureStuService.getMyLectures(userId, keyword, pageable));
    }

    @GetMapping("/{lectureId}")
    @ResponseBody
    public ResponseEntity<LectureStuDTO> getLectureDetail(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        return ResponseEntity.ok(lectureStuService.getLectureDetail(lectureId, userId));
    }

    @GetMapping("/{lectureId}/video")
    @ResponseBody
    public ResponseEntity<String> getLectureVideoUrl(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            return ResponseEntity.ok(lectureStuService.getVideoUrl(lectureId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{lectureId}/material")
    @ResponseBody
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            String materialId = lectureStuService.getMaterialId(lectureId, userId);
            byte[] fileData = "파일 내용 예시".getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialId + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new ByteArrayResource(fileData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}