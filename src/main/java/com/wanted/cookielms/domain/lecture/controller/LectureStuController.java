package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.annotation.CurrentUserId;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.dto.MyLectureListDTO; // 🌟 내 강의 DTO 추가됨
import com.wanted.cookielms.domain.lecture.exception.LectureErrorCode;
import com.wanted.cookielms.domain.lecture.exception.LectureException;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureStuService;

    // 강사와 동일한 경로 설정
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    // 비로그인 시 -1L 사용 (서비스 계약)
    private static long resolveUserId(Long userId) {
        return userId != null ? userId : -1L;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<MyLectureListDTO>> getLectures( // 🚀 MyLectureListDTO로 변경!
    @RequestParam(required = false) String keyword,
    @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
    }

    // 내 강의 목록 조회 API
    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<Page<MyLectureListDTO>> getMyLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable,
            @CurrentUserId Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(lectureStuService.getMyLectures(userId, keyword, pageable));
    }

    @GetMapping("/{lectureId}")
    @ResponseBody
    public ResponseEntity<LectureStuDTO> getLectureDetail(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        return ResponseEntity.ok(lectureStuService.getLectureDetail(lectureId, resolveUserId(userId)));
    }

    @GetMapping("/{lectureId}/video")
    @ResponseBody
    public ResponseEntity<String> getLectureVideoUrl(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        return ResponseEntity.ok(lectureStuService.getVideoUrl(lectureId, resolveUserId(userId)));
    }

    // 썸네일 이미지 API
    @GetMapping("/thumbnail/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = root.resolve(filename);
        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (Exception e) {
            // 경로가 이상해서 UrlResource 객체 생성이 안 될 때 AOP로 에러 던짐
            throw new LectureException(LectureErrorCode.FILE_NOT_FOUND);
        }

        if (!resource.exists() || !resource.isReadable()) {
            // 파일이 물리적으로 폴더에 없을 때 AOP로 에러 던짐
            throw new LectureException(LectureErrorCode.FILE_NOT_FOUND);
        }

        try {
            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }
    }

    // 강의 자료 다운로드 API
    @GetMapping("/{lectureId}/material")
    @ResponseBody
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, @CurrentUserId Long userId) {
        // 권한 체크 등은 이미 Service 단에서 LectureException을 던지고 있으니 통과!
        String materialFileName = lectureStuService.getMaterialId(lectureId, resolveUserId(userId));

        Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = root.resolve(materialFileName);
        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new LectureException(LectureErrorCode.FILE_NOT_FOUND);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new LectureException(LectureErrorCode.FILE_NOT_FOUND);
        }

        String encodedUploadFileName = UriUtils.encode(materialFileName, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedUploadFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
