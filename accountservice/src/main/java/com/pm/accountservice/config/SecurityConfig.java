package com.pm.accountservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    // este securityFilterChain es como un guardia hacia las peticiones HTTP
    // es un interceptor que se ejecuta cada vez que se haga una peticion, ejecuta el codigo
    // y basado en la logica de configuracion pues la peticion sigue su camino o no

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // el objetivo de este es dejar que todos puedan loggearse en el auth service
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // habilitando las peticiones HTTP a los endpoints libremente con permitAll()
        // prohibiendo requests como creacion de tenants o obtener un tenant si no esta autenticado
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/accounts/auth/login", "/accounts/auth/register")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                // agrega chains para agregar un filtro antes de que esto se ejecute y aplicar JWT validation con la clase de configuracion
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
