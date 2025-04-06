package com.pm.accountservice.config;

import com.pm.accountservice.model.User;
import com.pm.accountservice.service.UserService;
import com.pm.accountservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

       // extract token from Authorization header
       final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

       // if token null or doesnt start with 'Bearer ', continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // token exists, then retrieve without bearer
            // if exists validateToken
            // and assign role to that logged in user in JWT
            // else throw fordibben or unauthorized
            final String token = authHeader.substring(7);

            jwtUtil.validateToken(token);
            final String email = jwtUtil.getEmailFromToken(token);
            // get the roles and assign it to context
            List<String> roles = jwtUtil.getRoleFromToken(token);

            // create authority with correct role
            List<SimpleGrantedAuthority> authorities = roles
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();


            // get user from DB, could be null
            Optional<User> user = userService.findByEmail(email);

            // if there is a user but no authorization
            // set it in the security context
            if (user.isPresent() &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );

                authentication.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch(JwtException e) {
            logger.error("Invalid JWT token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "timestamp": "%s",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Invalid token."
                    }
                    """.formatted(LocalDateTime.now()));
            return;
        }

        // continue filter chain
        filterChain.doFilter(request, response);
    }
}
