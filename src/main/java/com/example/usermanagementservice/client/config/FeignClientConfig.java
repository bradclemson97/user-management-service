package com.example.usermanagementservice.client.config;

import feign.Logger.Level;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This Configuration class can be used for Spring Feign Clients interfaces for configuration.
 */
@Configuration
public class FeignClientConfig {

    @Value("${feign.log-level:HEADERS}")
    private Level level;

    @Bean
    Level feignLoggerLevel() { return level; }

    @Bean
    RequestInterceptor bearerTokenRelayInterceptor() {
        return template -> {
            try {
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpServletRequest request = attrs.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    template.header("Authorization", authHeader);
                }
            } catch (IllegalStateException ignored) {
                // No active servlet request (e.g. background threads)
            }
        };
    }
}
