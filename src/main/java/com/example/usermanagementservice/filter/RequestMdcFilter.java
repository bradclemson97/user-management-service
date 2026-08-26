package com.example.usermanagementservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(1)
public class RequestMdcFilter extends OncePerRequestFilter {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_HEADER))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());

        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_HEADER, correlationId);
        extractSystemUserId(request).ifPresent(id -> MDC.put("systemUserId", id));

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private Optional<String> extractSystemUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return Optional.empty();
        try {
            String token = auth.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length < 2) return Optional.empty();
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int idx = payload.indexOf("\"systemUserId\":\"");
            if (idx < 0) return Optional.empty();
            int start = idx + 16;
            int end = payload.indexOf('"', start);
            return end > start ? Optional.of(payload.substring(start, end)) : Optional.empty();
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
