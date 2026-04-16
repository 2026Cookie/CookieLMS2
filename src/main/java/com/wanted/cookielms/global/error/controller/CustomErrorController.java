package com.wanted.cookielms.global.error.controller;

import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.service.ErrorLogService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * [Phase 4] 최후의 방어선: ErrorController
 * 스프링 MVC(Advice)가 잡지 못하는 404, 401, 403 및 필터 에러를 처리합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private final ErrorLogService errorLogService;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 1. 에러 정보 추출 (Jakarta EE 표준 속성 사용)
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer status = (statusObj != null) ? Integer.valueOf(statusObj.toString()) : 500;

        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        // 2. 추적 ID 생성
        String traceId = UUID.randomUUID().toString();

        // 3. 에러 로깅 (필터 단에서 터진 에러도 우리 시스템에 기록!)
        saveErrorLog(status, message, requestUri, request, traceId, throwable);

        // 4. 사용자님이 만든 예쁜 HTML에 보낼 데이터 바인딩
        model.addAttribute("status", status);
        model.addAttribute("message", (message != null && !message.isEmpty()) ? message : getDefaultMessage(status));
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("traceId", traceId);
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 관리자 정보 (필요시 사용)
        model.addAttribute("adminEmail", "admin@cookielms.com");

        // 5. 상태별 뷰 라우팅
        if (status == 404) return "error/404";
        if (status >= 500) return "error/5xx";
        return "error/4xx"; // 400, 401, 403 등
    }

    /**
     * 필터/404 에러를 우리 에러 로그 시스템(Phase 3)에 전송
     */
    private void saveErrorLog(Integer status, String message, String requestUri,
                              HttpServletRequest request, String traceId, Throwable t) {
        try {
            // 500번대나 보안(401, 403) 에러는 CRITICAL로 격상
            ErrorSeverity severity = (status >= 500 || status == 401 || status == 403)
                    ? ErrorSeverity.CRITICAL : ErrorSeverity.WARNING;

            ErrorLogEntity errorLog = ErrorLogEntity.builder()
                    .errorCode(getErrorCodeForStatus(status))
                    .errorMessage(message != null ? message : getDefaultMessage(status))
                    .exceptionName(t != null ? t.getClass().getSimpleName() : "FilterOrNetworkError")
                    .requestUri(requestUri != null ? requestUri : request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .clientIp(getClientIp(request))
                    .userId("ANONYMOUS") // 나중에 시큐리티 연동 시 수정
                    .stackTrace(t != null ? t.toString() : "No StackTrace (Out of MVC)")
                    .traceId(traceId)
                    .severity(severity)
                    .build();

            errorLogService.saveErrorLogAsync(errorLog);
        } catch (Exception e) {
            log.error("Critical: Error logging failed in ErrorController", e);
        }
    }

    private String getErrorCodeForStatus(Integer status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            default -> "SERVER_ERROR";
        };
    }

    private String getDefaultMessage(Integer status) {
        return switch (status) {
            case 404 -> "페이지를 찾을 수 없습니다. 주소를 확인해주세요.";
            case 401, 403 -> "접근 권한이 없습니다. 로그인이 필요할 수 있습니다.";
            default -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
                ? request.getRemoteAddr() : ip.split(",")[0];
    }
}