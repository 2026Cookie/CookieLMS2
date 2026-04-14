package com.wanted.cookielms.domain.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureStuController {

    private final LectureStuService lectureService;

    @GetMapping
    public ResponseEntity<Page<LectureStuDTO>> getLectures(

            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<LectureStuDTO> response = lectureService.getAllLectures(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    
}