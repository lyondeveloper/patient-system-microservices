package com.pm.accountservice.service;

import com.pm.accountservice.config.CustomAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    public static String getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof CustomAuthenticationDetails details) {
            return details.getTenantId();
        }
        throw new IllegalStateException("TenantID not available in security context");
    }
}
