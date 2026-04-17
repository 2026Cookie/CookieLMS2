package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page; // 🌟 잃어버린 임포트 복구!
import org.springframework.data.domain.Pageable; // 🌟 잃어버린 임포트 복구!
import org.springframework.data.web.PageableDefault; // 🌟 잃어버린 임포트 복구!
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
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

    // 🌟🌟🌟 제가 실수로 빼먹었던 바로 그 '목록 조회 API' 입니다!! 🌟🌟🌟
    @GetMapping
    public ResponseEntity<Page<LectureStuDTO>> getLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureStuDTO> getLectureDetail(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        return ResponseEntity.ok(lectureStuService.getLectureDetail(lectureId, userId));
    }

    @GetMapping("/{lectureId}/video")
    public ResponseEntity<String> getLectureVideoUrl(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            return ResponseEntity.ok(lectureStuService.getVideoUrl(lectureId, userId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{lectureId}/material")
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            String materialId = lectureStuService.getMaterialId(lectureId, userId);
            byte[] fileData = "파일 내용 예시".getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialId + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new ByteArrayResource(fileData));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}