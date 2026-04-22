package com.wanted.cookielms.global.error.controller;

import com.wanted.cookielms.global.logging.error.entity.ErrorLogEntity;
import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.logging.error.service.ErrorLogService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.MDC;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * [Phase 4] 최후의 방어선: ErrorController
 * 스프링 MVC(Advice)가 잡지 못하는 에러를 처리합니다.
 * (404, 401, 403, 500 등 모든 에러를 business-error.html로 통일 처리)
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
// 쿼리 파라미터에서 먼저 확인, 없으면 MDC에서, 그것도 없으면 생성
// 변경
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        // 4. 사용자님이 만든 예쁜 HTML에 보낼 데이터 바인딩
        model.addAttribute("status", status);
        model.addAttribute("message", (message != null && !message.isEmpty()) ? message : getDefaultMessage(status));
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("traceId", traceId);
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 관리자 정보 (필요시 사용)
        model.addAttribute("adminEmail", "admin@cookielms.com");

        // 5. 모든 에러를 business-error.html로 통일
        return "error/business-error";
    }

    private String getDefaultMessage(Integer status) {
        return switch (status) {
            case 404 -> "페이지를 찾을 수 없습니다. 주소를 확인해주세요.";
            case 401, 403 -> "접근 권한이 없습니다. 로그인이 필요합니다.";
            default -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        };
    }

}