package com.pm.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationGatewayFilterFactory.class);
    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${account.service.url}") String accountServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(accountServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // getting token from Authorization header
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                // throw 401 and complete the request
                log.warn("Authorization header is missing or invalid. Returning 401 Unauthorized");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                    .uri("/auth/validateCurrentToken")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .exchangeToMono(clientResponse -> {
                        // Log the response status
                        HttpStatusCode statusCode = clientResponse.statusCode();
                        log.debug("Token validation response status: {}", statusCode);

                        if (clientResponse.statusCode().is4xxClientError()) {
                            // Enhance logging with more details
                            return clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("No response body")
                                    .flatMap(body -> {
                                        log.warn("Invalid token, 401 returned. Response body: {}", body);
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        return exchange.getResponse().setComplete();
                                    });
                        }

                        if (clientResponse.statusCode().is5xxServerError()) {
                            // Enhance 5xx error logging
                            return clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("No error details available")
                                    .flatMap(errorBody -> {
                                        log.error("Unexpected error during token validation, returning 500. Error details: {}", errorBody);
                                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                        return exchange.getResponse().setComplete();
                                    });
                        }

                        return clientResponse.bodyToMono(Void.class)
                                .then(Mono.defer(() -> chain.filter(exchange)));
                    })
                    // Handle unexpected errors (timeouts, etc.)
                    .onErrorResume(
                            throwable -> {
                                // Mejorar el logging del error específico
                                if (throwable.getMessage().contains("SecurityContext")) {
                                    log.warn("Security context error during token validation: {}",
                                            throwable.getMessage(), throwable);
                                } else {
                                    log.error("Unexpected error during token validation", throwable);
                                }
                                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return exchange.getResponse().setComplete();
                            }
                    );
        };
    }
}
