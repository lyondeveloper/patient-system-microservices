package com.pm.accountservice.config;

import org.springframework.http.server.reactive.ServerHttpRequest;

public record CustomAuthenticationDetails(Long tenantId, ServerHttpRequest request) {
}
