package com.ptc.halo.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = {StudentMentorController.class, StudentAiLearningController.class,
        StudentLearningProgressionController.class, StudentAssessmentController.class})
public class StudentLessonExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(StudentLessonExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException error, HttpServletRequest request) {
        return response(error.getStatusCode().value(), error.getReason() == null ? "REQUEST_FAILED" : error.getReason(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> denied(AccessDeniedException error, HttpServletRequest request) {
        return response(403, "INVALID_ROLE_OR_ACCESS", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unexpected(Exception error, HttpServletRequest request) {
        // Do not expose credentials, upstream payloads or stack traces to the browser.
        log.error("request_failed method={} path={} exceptionType={}", request.getMethod(),
                request.getRequestURI(), error.getClass().getName());
        return response(500, "INTERNAL_SERVER_ERROR", request);
    }

    private ResponseEntity<Map<String, Object>> response(int status, String code, HttpServletRequest request) {
        log.warn("request_rejected method={} path={} status={} reason={}",
                request.getMethod(), request.getRequestURI(), status, code);
        return ResponseEntity.status(status).body(Map.of("status", status, "code", code, "message", code));
    }
}
