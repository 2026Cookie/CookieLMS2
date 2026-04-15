package com.wanted.cookielms.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 모든 HTTP 요청에 고유한 Trace ID를 생성하고 MDC에 저장합니다.
 * 같은 요청 내에서 발생하는 여러 에러를 추적하기 위해 사용됩니다.
 *
 * 예시:
 * - 요청 1: TraceId = "a1b2c3d4-e5f6-47g8-h9i0-j1k2l3m4n5o6"
 * - 이 요청 처리 중 2개 에러 발생 → 두 에러 모두 같은 TraceId 기록
 * - 관리자가 "a1b2c3d4..." 로 조회하면 요청의 모든 에러를 한번에 볼 수 있음
 */
@Slf4j
@Component
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 요청 헤더에서 Trace ID 추출 (있으면 사용, 없으면 새로 생성)
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        // 2. MDC에 Trace ID 저장 (로깅에서 자동으로 사용됨)
        MDC.put(TRACE_ID_KEY, traceId);

        // 3. 응답 헤더에 Trace ID 추가 (클라이언트가 에러 추적 가능)
        response.addHeader("X-Trace-Id", traceId);

        try {
            // 4. 요청 처리
            filterChain.doFilter(request, response);
        } finally {
            // 5. MDC 정리 (메모리 누수 방지)
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
