package com.wanted.cookielms.domain.lecture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class InsFileService {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    public String storeFile(MultipartFile file, String... allowedExtensions) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

        if (!Arrays.asList(allowedExtensions).contains(ext)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        long maxSize = 20 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 용량은 20MB를 초과할 수 없습니다.");
        }

        String savedName = UUID.randomUUID().toString() + ext;

        // NIO 방식: 인텔리제이 프로젝트 폴더 위치를 정확히 추적하여 uploads 폴더 지정
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path target = uploadDir.resolve(savedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return savedName;
    }
}