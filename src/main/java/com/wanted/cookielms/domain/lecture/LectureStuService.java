package com.wanted.cookielms.domain.lecture;

import com.wanted.cookielms.domain.lecture.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.LectureStuEntity;
import com.wanted.cookielms.domain.lecture.LectureStuRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureStuService {

    private final LectureStuRepository lectureStuRepository;
    private final ModelMapper modelMapper;

    public Page<LectureStuDTO> getAllLectures(String keyword, Pageable pageable) {
        Page<LectureStuEntity> lectures;

        // 🌟 검색어가 없거나 공백이면 전체 조회, 검색어가 있으면 필터링 조회
        if (keyword == null || keyword.trim().isEmpty()) {
            lectures = lectureStuRepository.findAll(pageable);
        } else {
            lectures = lectureStuRepository.findByTitleContaining(keyword, pageable);
        }

        // Entity -> DTO 변환은 그대로!
        return lectures.map(entity -> modelMapper.map(entity, LectureStuDTO.class));
    }
}