package com.wanted.cookielms.global.config.security.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 리소스 소유권 검증을 위한 애너테이션
 * resourceType: 검증 대상 도메인 (예: "ASSIGNMENT", "ENROLLMENT")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnershipVerification {
    String resourceType();
}