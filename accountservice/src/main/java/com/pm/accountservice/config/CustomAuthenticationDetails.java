package com.pm.accountservice.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.Serializable;

@Getter
public class CustomAuthenticationDetails implements Serializable {
    private final String tenantId;
    private final WebAuthenticationDetails webDetails;

    public CustomAuthenticationDetails(String tenantId, HttpServletRequest request) {
        this.tenantId = tenantId;
        this.webDetails = new WebAuthenticationDetailsSource().buildDetails(request);
    }
}
