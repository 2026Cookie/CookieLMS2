package com.wanted.cookielms.domain.lecture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class InsFileService {

    // application.yml에서 경로를 읽어옵니다.
    @Value("${file.upload.path:C:/cookielms/uploads/}")
    private String uploadPath;

    /**
     * 파일 저장 공통 로직
     * @param file 업로드된 파일
     * @param allowedExtension 허용할 확장자 (예: ".pdf", ".jpg")
     * @return 저장된 UUID 파일명
     */
    public String storeFile(MultipartFile file, String allowedExtension) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();

        // 1. 확장자 검증
        if (originalName == null || !originalName.toLowerCase().endsWith(allowedExtension)) {
            throw new IllegalArgumentException(allowedExtension.toUpperCase() + " 형식의 파일만 업로드 가능합니다.");
        }

        // 2. 용량 검증 (5MB 제한)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 용량은 5MB를 초과할 수 없습니다.");
        }

        // 3. 저장용 고유 이름 생성 (UUID)
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + ext;

        // 4. 폴더 생성 및 파일 저장
        File target = new File(uploadPath, savedName);
        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        file.transferTo(target);

        return savedName;
    }
}