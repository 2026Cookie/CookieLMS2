package com.wanted.cookielms.global.interceptor;

import com.wanted.cookielms.domain.admin.dto.ApiPerformanceLogDto;
import com.wanted.cookielms.domain.admin.enums.HttpMethod;
import com.wanted.cookielms.domain.admin.service.ApiPerformanceLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiTrackingInterceptor implements HandlerInterceptor {

    private final ApiPerformanceLogService apiPerformanceLogService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        if (!(handler instanceof HandlerMethod)) return;

        Long startTime = (Long) request.getAttribute("startTime");
        int latencyMs = startTime != null
                ? (int) (System.currentTimeMillis() - startTime) : 0;

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            httpMethod = HttpMethod.GET;
        }

        ApiPerformanceLogDto dto = new ApiPerformanceLogDto();
        dto.setEndpoint(request.getRequestURI());
        dto.setHttpMethod(httpMethod);
        dto.setExecutionTimeMs(latencyMs);
        dto.setCreatedAt(LocalDateTime.now());

        apiPerformanceLogService.saveAsync(dto);
    }
}