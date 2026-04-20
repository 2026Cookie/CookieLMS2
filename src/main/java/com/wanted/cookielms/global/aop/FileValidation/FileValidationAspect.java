package com.wanted.cookielms.global.aop.FileValidation;

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

                if (file.getSize() > maxSizeBytes) {
                    throw new FileValidationException(FileValidationErrorCode.FILE_SIZE_EXCEEDED);
                }

                if (allowedExts.length > 0) {
                    String originalName = file.getOriginalFilename();
                    String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
                    if (!Arrays.asList(allowedExts).contains(ext)) {
                        throw new FileValidationException(FileValidationErrorCode.INVALID_FILE_EXTENSION);
                    }
                }

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