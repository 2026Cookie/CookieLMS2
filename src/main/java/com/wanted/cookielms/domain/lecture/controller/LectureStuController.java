package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

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

    // 수강생 전용 영상 URL 조회 API
    @GetMapping("/{lectureId}/video")
    public ResponseEntity<String> getLectureVideoUrl(@PathVariable Long lectureId) {
        try {
            String videoUrl = lectureStuService.getVideoUrl(lectureId);
            return ResponseEntity.ok(videoUrl);
        } catch (SecurityException e) {
            // 권한이 없으면 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 수강생 전용 학습 자료 다운로드 API
    @GetMapping("/{lectureId}/material")
    public ResponseEntity<Resource> downloadLectureMaterial(@PathVariable Long lectureId) {
        try {
            // 1. 서비스에서 권한 체크 및 파일명(material_id) 가져오기
            String materialId = lectureStuService.getMaterialId(lectureId);

            // 2. [임시 파일 생성] 실제 S3나 로컬 경로 대신, 다운로드 테스트를 위한 가짜 데이터를 만듭니다.
            // 나중에 이 부분을 S3 연동 코드(예: s3Service.download(materialId))로 교체하시면 됩니다!
            String mockContent = "이것은 [" + materialId + "] 파일의 내용입니다.\n나중에 실제 파일 데이터로 교체될 예정입니다!";
            byte[] fileData = mockContent.getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(fileData);

            // 3. 브라우저가 "다운로드" 하도록 헤더 세팅
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialId + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileData.length)
                    .body(resource);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}