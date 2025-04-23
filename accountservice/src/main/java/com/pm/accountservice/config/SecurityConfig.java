package com.pm.accountservice.config;

import com.pm.accountservice.service.UserService;
import com.pm.accountservice.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    // este securityFilterChain es como un guardia hacia las peticiones HTTP
    // es un interceptor que se ejecuta cada vez que se haga una peticion, ejecuta el codigo
    // y basado en la logica de configuracion pues la peticion sigue su camino o no

    private final JwtUtil jwtUtil; // Asegúrate que JwtUtil esté registrado como un bean en tu aplicación
    private final UserService userService; // Igual para UserService

    public SecurityConfig(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userService, securityContextRepository());
    }


    // metodo para manejar los filtros de seguridad y autenticacion
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // habilitando las peticiones HTTP a los endpoints libremente con permitAll()
        // prohibiendo requests como creacion de tenants o obtener un tenant si no esta autenticado


        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/accounts/auth/login", "/accounts/auth/register").permitAll()
                        .anyExchange().authenticated())
                .securityContextRepository(securityContextRepository())
                .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(customAccessDeniedHandler()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerAccessDeniedHandler customAccessDeniedHandler() {
        return (ServerWebExchange exchange, AccessDeniedException ex) -> {
            // HTTP to 403
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String responseBody = """
            {
              "timestamp": "%s",
              "status": 403,
              "error": "Access Denied",
              "message": "You don't have permission to perform this operation, only an ADMIN can do this."
            }
            """.formatted(LocalDateTime.now());

            // Write the response body reactively
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(responseBody.getBytes())));

        };
    }
}
