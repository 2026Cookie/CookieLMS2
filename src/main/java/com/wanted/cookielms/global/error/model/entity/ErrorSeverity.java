package com.wanted.cookielms.global.error.model.entity;

import lombok.Getter;

/**
 * 에러 심각도 레벨 정의
 */
@Getter
public enum ErrorSeverity {
    /**
     * INFO: 사용자 실수로 인한 일반적인 에러 (예: 잘못된 입력값)
     * → 모니터링 필요 없음, 로그만 기록
     */
    INFO("정보", 1),

    /**
     * WARNING: 시스템 기능 장애 (예: 강의 없음, 수강인원 초과)
     * → 로그 기록 + 모니터링 권장
     */
    WARNING("경고", 2),

    /**
     * CRITICAL: 보안/인증 문제, 서버 내부 에러 (예: 로그인 필요, 권한 없음, 500 에러)
     * → 즉시 알림 필요
     */
    CRITICAL("심각", 3);

    private final String displayName;
    private final int priority;  // 우선순위 (높을수록 심각함)

    ErrorSeverity(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }
}
