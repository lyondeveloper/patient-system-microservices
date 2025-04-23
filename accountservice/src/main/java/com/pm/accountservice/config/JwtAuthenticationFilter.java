package com.pm.accountservice.config;

import com.pm.accountservice.service.UserService;
import com.pm.accountservice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ServerSecurityContextRepository securityContextRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService, ServerSecurityContextRepository securityContextRepository) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("JwtAuthenticationFilter.filter called for path: {}",
                exchange.getRequest().getPath());

        ServerHttpRequest request = exchange.getRequest();

       // extract token from Authorization header
       final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

       // if token null or doesn't start with 'Bearer ', continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("No Authorization header or not Bearer token");
            return chain.filter(exchange);
        }

        // token exists, then retrieve without bearer
        // if exists validateToken
        // and assign role to that logged in user in JWT
        // else throw fordibben or unauthorized
        final String token = authHeader.substring(7);
        final String email;
        final Long tenantId;
        final List<String> roles;
        try {
            email = jwtUtil.getEmailFromToken(token);
            tenantId = jwtUtil.getTenantIdFromToken(token);
            roles = jwtUtil.getRoleFromToken(token);
            log.info("Token data extracted for user: {}", email);
        } catch (Exception ex) {
            log.error("Error extracting data from token: {}", ex.getMessage());
            return chain.filter(exchange);
        }
        // get user from DB, could be null
        // if there is a user but no authorization
        // set it in the security context
        return userService.findUserByEmailAndTenantId(email, tenantId)
                .flatMap(u -> {
                    if (jwtUtil.isTokenValid(token)) {
                        // create authority with correct role
                        List<SimpleGrantedAuthority> authorities = roles
                                .stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .toList();
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                        auth.setDetails(new CustomAuthenticationDetails(tenantId, request));

                        SecurityContext securityContext = new SecurityContextImpl(auth);
                        log.info("Security context created for user: {}", email);

                        return securityContextRepository.save(exchange, securityContext)
                                .then(
                                        chain.filter(exchange)
                                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                                );
                    }
                    return Mono.empty();
                })
                .onErrorResume((ex) -> {
                    log.error("User not found for email: {}", email);
                    return chain.filter(exchange);
                });
    }
}
