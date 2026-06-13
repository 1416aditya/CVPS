package com.heg.cvps.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    // Define the unique API Key you will pass to your front-end teammate
    private static final String ASSIGNED_API_KEY = "HEG-CVPS-KEY-TOKEN-2026";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String providedHeaderKey = request.getHeader("X-API-KEY");

        // If the header is missing or doesn't match, block the execution instantly
        if (providedHeaderKey == null || !providedHeaderKey.equals(ASSIGNED_API_KEY)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized Request\", \"message\": \"Missing or invalid API Key access header Token.\"}");
            return false; 
        }
        return true; // Token validation succeeded, pass request to Controller
    }
}