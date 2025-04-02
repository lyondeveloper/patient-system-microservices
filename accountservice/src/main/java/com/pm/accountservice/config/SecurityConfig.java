package com.pm.accountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // este securityFilterChain es como un guardia hacia las peticiones HTTP
    // es un interceptor que se ejecuta cada vez que se haga una peticion, ejecuta el codigo
    // y basado en la logica de configuracion pues la peticion sigue su camino o no

    // el objetivo de este es dejar que todos puedan loggearse en el auth service
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // habilitando las peticiones HTTP a los endpoints libremente con permitAll()
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
