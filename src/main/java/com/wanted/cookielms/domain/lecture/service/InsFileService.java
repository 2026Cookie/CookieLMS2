package com.wanted.cookielms.domain.lecture.service;

import com.wanted.cookielms.global.aop.FileValidation.FileValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class InsFileService {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @FileValidation(maxSize = 20, allowedExtensions = {".pdf", ".jpg", ".png", ".jpeg", ".gif"})
    public String storeFile(MultipartFile file, String... allowedExtensions) throws IOException {
        // 빈 파일 + 크기 + 확장자 검증은 AOP에서 처리됨

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

        String savedName = UUID.randomUUID().toString() + ext;

        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path target = uploadDir.resolve(savedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return savedName;
    }

}