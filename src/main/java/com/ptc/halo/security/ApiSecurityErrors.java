package com.ptc.halo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiSecurityErrors {
    private static final Logger log = LoggerFactory.getLogger(ApiSecurityErrors.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private ApiSecurityErrors() {}

    public static void write(HttpServletRequest request, HttpServletResponse response,
                             int status, String code) throws IOException {
        log.warn("security_denied method={} path={} status={} reason={}",
                request.getMethod(), request.getRequestURI(), status, code);
        response.setStatus(status);
        response.setContentType("application/json");
        mapper.writeValue(response.getOutputStream(), Map.of("status", status, "code", code, "message", code));
    }
}
