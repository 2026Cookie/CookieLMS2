package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<LectureStuDTO>> getLectures(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.ok(lectureStuService.getAllLectures(keyword, pageable));
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

    @GetMapping("/{lectureId}/material")
    @ResponseBody
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        String materialId = lectureStuService.getMaterialId(lectureId, userId);
        byte[] fileData = "파일 내용 예시".getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(fileData));
    }
}
