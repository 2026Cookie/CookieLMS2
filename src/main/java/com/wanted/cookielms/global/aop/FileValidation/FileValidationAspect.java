package com.wanted.cookielms.global.aop.FileValidation;

// 🌟 AlertException import 추가
import com.wanted.cookielms.global.aop.FileValidation.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FileValidationAspect {

    @Around("@annotation(fileValidation)")
    public Object validateFile(ProceedingJoinPoint joinPoint, FileValidation fileValidation) throws Throwable {
        Object[] args = joinPoint.getArgs();
        long maxSizeBytes = fileValidation.maxSize() * 1024 * 1024;
        String[] allowedExts = fileValidation.allowedExtensions();
        String[] allowedMimeTypes = fileValidation.allowedMimeTypes();

        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;

                if (file == null || file.isEmpty()) {
                    return null;
                }

                // 🌟 수정 1: 파일 크기 초과
                if (file.getSize() > maxSizeBytes) {
                    throw new FileValidationException(FileValidationErrorCode.FILE_SIZE_EXCEEDED);
                }

                // 🌟 수정 2: 허용되지 않은 확장자
                if (allowedExts.length > 0) {
                    String originalName = file.getOriginalFilename();
                    String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
                    if (!Arrays.asList(allowedExts).contains(ext)) {
                        throw new FileValidationException(FileValidationErrorCode.INVALID_FILE_EXTENSION);
                    }
                }

                // 🌟 수정 3: 허용되지 않은 MIME 타입
                if (allowedMimeTypes.length > 0) {
                    String contentType = file.getContentType();
                    if (contentType == null || !Arrays.asList(allowedMimeTypes).contains(contentType)) {
                        throw new FileValidationException(FileValidationErrorCode.INVALID_FILE_FORMAT);
                    }
                }
            }
        }

        return joinPoint.proceed();
    }
}