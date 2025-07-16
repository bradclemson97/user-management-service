package com.example.usermanagementservice.client;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * This Configuration class can be used for Spring Feign Clients interfaces for configuration.
 */
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    @Value("${feign.log-level:HEADERS}")
    private final Logger.Level level;

    @Bean
    RequestInterceptor feignClientInterceptor() {
        return template -> {
            Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .filter(ServletRequestAttributes.class)
        }
    }
}
