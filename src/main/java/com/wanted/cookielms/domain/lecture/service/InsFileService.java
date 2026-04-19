package com.wanted.cookielms.domain.lecture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Service
public class InsFileService {

    @Value("${file.upload.path:C:/cookielms/uploads/}")
    private String uploadPath;

    // 🌟 String... 을 사용하면 파라미터를 1개, 2개, 3개 내맘대로 넣을 수 있습니다!
    public String storeFile(MultipartFile file, String... allowedExtensions) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();

        // 1. 확장자 검증 (배열 안에 해당 확장자가 포함되어 있는지 확인)
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(allowedExtensions).contains(ext)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 허용되는 확장자: " + Arrays.toString(allowedExtensions));
        }

        // 2. 용량 검증 (20MB 제한)
        long maxSize = 20 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 용량은 20MB를 초과할 수 없습니다.");
        }

        // 3. 겹치지 않는 새 이름(UUID) 만들기
        String savedName = UUID.randomUUID().toString() + ext;

        // 4. 진짜로 폴더에 저장하기
        File target = new File(uploadPath, savedName);
        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        file.transferTo(target);

        return savedName;
    }
}