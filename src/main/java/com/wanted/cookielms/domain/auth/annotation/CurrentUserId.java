package com.wanted.cookielms.domain.auth.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "loginUserDTO.userId")
public @interface CurrentUserId {

    @AliasFor(annotation = AuthenticationPrincipal.class, attribute = "errorOnInvalidType")
    boolean errorOnInvalidType() default false;
}
