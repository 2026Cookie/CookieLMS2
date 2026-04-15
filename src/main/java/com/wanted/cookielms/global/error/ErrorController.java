package com.wanted.cookielms.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View 요청의 에러를 처리하여 HTML 페이지를 렌더링하는 컨트롤러
 * (API 에러는 GlobalExceptionHandler에서 JSON으로 처리)
 */
@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * /error 경로로 오는 모든 요청을 처리
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = (String) request.getAttribute("javax.servlet.error.message");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        // status_code가 null인 경우 500으로 기본값 설정
        if (status == null) {
            status = 500;
        }

        log.warn("Error occurred - Status: {}, Message: {}, URI: {}", status, message, requestUri);

        // Model에 에러 정보 추가
        model.addAttribute("status", status);
        model.addAttribute("message", message != null ? message : getDefaultMessage(status));
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("traceId", MDC.get("traceId"));

        // 상태 코드별 뷰 선택
        if (status >= 500) {
            return "error/5xx";
        } else if (status == 404) {
            return "error/404";
        } else {
            return "error/4xx";
        }
    }

    /**
     * 상태 코드별 기본 메시지 제공
     */
    private String getDefaultMessage(Integer status) {
        return switch (status) {
            case 400 -> "잘못된 요청입니다.";
            case 401 -> "인증이 필요합니다.";
            case 403 -> "접근 권한이 없습니다.";
            case 404 -> "요청한 리소스를 찾을 수 없습니다.";
            case 409 -> "요청이 현재 상태와 충돌합니다.";
            case 500 -> "서버 내부 오류가 발생했습니다.";
            default -> "오류가 발생했습니다.";
        };
    }

    /**
     * ErrorController 인터페이스 구현 - 에러 경로 반환
     */
    public String getErrorPath() {
        return "/error";
    }
}
