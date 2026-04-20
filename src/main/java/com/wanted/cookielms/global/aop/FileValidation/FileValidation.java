package com.wanted.cookielms.global.aop.FileValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileValidation {
    long maxSize() default 20; // MB
    String[] allowedExtensions() default {};
    String[] allowedMimeTypes() default {}; // 추가
}