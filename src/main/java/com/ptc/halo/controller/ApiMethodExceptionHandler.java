package com.ptc.halo.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Method mismatches happen before Spring can select a controller.
@RestControllerAdvice
public class ApiMethodExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiMethodExceptionHandler.class);
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> method(HttpRequestMethodNotSupportedException error, HttpServletRequest request) {
        log.warn("request_rejected method={} path={} status=405 reason=METHOD_NOT_ALLOWED",
                request.getMethod(), request.getRequestURI());
        return ResponseEntity.status(405).header("Allow", String.join(", ", error.getSupportedMethods() == null ? new String[0] : error.getSupportedMethods()))
                .body(Map.of("status", 405, "code", "METHOD_NOT_ALLOWED", "message", "Use the supported HTTP method."));
    }
}
