package com.wanted.cookielms.global.error.handler;

import com.wanted.cookielms.global.logging.error.entity.enums.ErrorSeverity;
import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
    ErrorSeverity getSeverity();
}