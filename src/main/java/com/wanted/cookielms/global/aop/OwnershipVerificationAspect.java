package com.wanted.cookielms.global.aop;

import com.wanted.cookielms.domain.assignment.service.AssignmentStuService;
import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.global.error.handler.ApplicationException;
import com.wanted.cookielms.global.error.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipVerificationAspect {

    private final AssignmentStuService assignmentStuService;

    @Before("@annotation(ownershipVerification)")
    public void verify(JoinPoint joinPoint, OwnershipVerification ownershipVerification) {
        Long currentUserId = getCurrentUserId();
        Long resourceId = getResourceIdFromArgs(joinPoint);

        validateOwnership(ownershipVerification.resourceType(), resourceId, currentUserId);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 컨트롤러와 동일하게 AuthDetails로 캐스팅하여 PK 추출
        if (auth != null && auth.getPrincipal() instanceof AuthDetails authDetails) {
            return authDetails.getLoginUserDTO().getUserId();
        }
        throw new ApplicationException(ErrorCode.UNAUTHORIZED);
    }

    private Long getResourceIdFromArgs(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = signature.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                if (args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }
        }
        throw new ApplicationException(ErrorCode.BAD_REQUEST);
    }

    private void validateOwnership(String resourceType, Long resourceId, Long userId) {
        switch (resourceType) {
            case "ASSIGNMENT":
                assignmentStuService.validateAssignmentAccess(resourceId, userId);
                break;
            default:
                throw new ApplicationException(ErrorCode.BAD_REQUEST);
        }
    }
}