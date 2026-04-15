package com.wanted.cookielms.global.error.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 다양한 프록시 환경에서 클라이언트 IP를 안전하게 추출합니다.
 */
@Slf4j
@Component
public class ClientIpResolver {

    /**
     * 요청의 실제 클라이언트 IP를 추출합니다.
     *
     * 우선순위:
     * 1. X-Forwarded-For (가장 일반적)
     * 2. CF-Connecting-IP (Cloudflare)
     * 3. X-Real-IP (Nginx)
     * 4. RemoteAddr (직접 연결)
     */
    public String resolveClientIp(HttpServletRequest request) {
        // 1. X-Forwarded-For 확인 (가장 왼쪽 IP가 실제 클라이언트)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // 2. Cloudflare IP
        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isEmpty()) {
            return cfConnectingIp;
        }

        // 3. Nginx X-Real-IP
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // 4. 직접 연결
        return request.getRemoteAddr();
    }
}