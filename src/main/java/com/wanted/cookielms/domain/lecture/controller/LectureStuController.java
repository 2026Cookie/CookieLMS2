package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureStuService;

    // 🌟 강사님이 올린 자료가 저장되는 기본 경로
    @Value("${file.upload.path:C:/cookielms/uploads/}")
    private String uploadPath;

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

    // 🌟 껍데기 파일 대신 로컬 저장소에서 진짜 파일 꺼내오기!
    @GetMapping("/{lectureId}/material")
    @ResponseBody
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            // DB에서 저장된 진짜 파일 이름(materialId) 가져오기
            String savedFileName = lectureStuService.getMaterialId(lectureId, userId);

            // 물리적인 파일 위치 탐색
            Path filePath = Paths.get(uploadPath, savedFileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 한글 파일명 깨짐 방지 인코딩 처리
            String encodedUploadFileName = UriUtils.encode(savedFileName, StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}