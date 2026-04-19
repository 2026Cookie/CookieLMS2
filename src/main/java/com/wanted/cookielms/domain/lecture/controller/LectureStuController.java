package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO; // 🌟 내 강의 DTO 추가됨
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureStuService;

    // 🌟 1. 강사와 동일한 경로 설정
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    private Long getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return -1L;
        }
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return authDetails.getLoginUserDTO().getUserId();
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<LectureStuDTO>> getLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
    }

    // 🌟 [추가됨] 예전 코드에서 빠졌던 '내 강의 목록' 조회 API 복구
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
        return ResponseEntity.ok(lectureStuService.getVideoUrl(lectureId, userId));
    }

    // 🌟 2. 진짜 강의 자료 다운로드 로직으로 교체
    @GetMapping("/{lectureId}/material")
    @ResponseBody
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        try {
            String materialFileName = lectureStuService.getMaterialId(lectureId, userId);

            Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path filePath = root.resolve(materialFileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String encodedUploadFileName = UriUtils.encode(materialFileName, StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedUploadFileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 🌟 3. [추가됨] 썸네일 이미지를 화면에 띄워주는 API
    @GetMapping("/thumbnail/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        try {
            Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path filePath = root.resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}