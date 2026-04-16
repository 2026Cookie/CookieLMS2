package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.error.service.ErrorLogService;
import com.wanted.cookielms.global.error.model.DTO.ErrorResponseDTO;
import com.wanted.cookielms.global.error.model.entity.ErrorLogEntity;
import com.wanted.cookielms.global.error.model.entity.enums.ErrorSeverity;
import com.wanted.cookielms.global.error.model.repository.ErrorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService; // ⭕ Service 인스턴스를 주입받

    /**
     * [1] 비즈니스 로직 예외 처리 (우리가 만든 커스텀 예외)
     */
    @ExceptionHandler(ApplicationException.class) // BusinessException의 부모 클래스
    public ResponseEntity<ErrorResponseDTO> handleApplicationException(ApplicationException e, HttpServletRequest request) {
        String traceId = generateTraceId();

        // 커스텀 예외가 품고 있는 정보로 로깅 및 응답
        saveErrorLog(e, e.getCode(), e.getMessage(), request, traceId, e.getSeverity());

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(e.getStatus().value())
                .code(e.getCode())
                .message(e.getMessage())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    /**
     * [2] @Valid, @Validated 바인딩 에러 (400)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponseDTO> handleValidationException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        // 첫 번째 필드 에러 메시지만 추출 (실무에서는 List로 모두 담아주기도 함)
        String errorMessage = "입력값 검증에 실패했습니다.";
        if (e instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        saveErrorLog(e, errorCode.getCode(), errorMessage, request, traceId, errorCode.getSeverity());
        return createResponseEntity(errorCode, errorMessage, traceId);
    }

    /**
     * [3] 지원하지 않는 HTTP 메서드 호출 (405 -> 400으로 통합 또는 커스텀 처리)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        String errorMessage = "지원하지 않는 HTTP 메서드입니다: " + e.getMethod();

        saveErrorLog(e, errorCode.getCode(), errorMessage, request, traceId, errorCode.getSeverity());
        return createResponseEntity(errorCode, errorMessage, traceId);
    }

    /**
     * [4] 존재하지 않는 API 호출 (404)
     * (주의: application.yml에서 spring.mvc.throw-exception-if-no-handler-found=true 설정 필요)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.NOT_FOUND;

        saveErrorLog(e, errorCode.getCode(), errorCode.getMessage(), request, traceId, errorCode.getSeverity());
        return createResponseEntity(errorCode, errorCode.getMessage(), traceId);
    }

    /**
     * [5] 그 외 모든 예상치 못한 서버 에러 (500)
     * 시스템에서 놓친 에러는 모두 여기서 잡힙니다. (최후의 보루)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnhandledException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("[UnHandled Exception] traceId: {}, message: {}", traceId, e.getMessage(), e);
        saveErrorLog(e, errorCode.getCode(), errorCode.getMessage(), request, traceId, errorCode.getSeverity());

        return createResponseEntity(errorCode, errorCode.getMessage(), traceId);
    }

    // =========================================================================
    // Private Helper Methods
    // =========================================================================

    /**
     * 공통 에러 응답 생성기
     */
    private ResponseEntity<ErrorResponseDTO> createResponseEntity(ErrorCode errorCode, String message, String traceId) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(message) // ErrorCode의 기본 메시지 대신 커스텀 메시지 주입 가능
                .traceId(traceId)
                .build();
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * 에러 로그 DB 저장 로직 (Phase 2 엔티티 활용)
     */
    private void saveErrorLog(Exception e, String errorCodeString, String errorMessage,
                              HttpServletRequest request, String traceId, ErrorSeverity severity) {
        try {
            ErrorLogEntity errorLog = ErrorLogEntity.builder()
                    .errorCode(errorCodeString)
                    .errorMessage(errorMessage)
                    .exceptionName(e.getClass().getSimpleName())
                    .requestUri(request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .clientIp(getClientIp(request))
                    .userId(getCurrentUserId()) // TODO: Security 연동 (Phase 4)
                    .stackTrace(getStackTraceAsString(e))
                    .traceId(traceId)
                    .severity(severity)
                    .build();

            errorLogService.saveErrorLogAsync(errorLog);
        } catch (Exception logException) {
            // 로깅하다가 발생한 에러 때문에 본래 에러 응답이 묻히지 않도록 방어
            log.error("Error logging failed", logException);
        }
    }

    /**
     * StackTrace를 Text로 변환
     */
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Proxy 환경(AWS ALB, Nginx 등)을 고려한 실제 Client IP 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 여러 IP가 쉼표로 구분되어 들어올 경우 첫 번째 IP가 실제 클라이언트
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }

    /**
     * 추적 ID 생성
     * (실무에서는 보통 Filter/Interceptor에서 MDC에 넣고 빼서 씁니다. 일단 임시로 UUID 생성)
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Security Context에서 현재 사용자 ID 추출 (임시)
     */
    private String getCurrentUserId() {
        // Phase 4에서 SecurityContextHolder.getContext().getAuthentication() 을 통해 가져오도록 수정
        return "ANONYMOUS";
    }
}