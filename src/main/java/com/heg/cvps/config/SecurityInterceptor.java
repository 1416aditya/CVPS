


package com.heg.cvps.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.heg.cvps.entity.CvpsUser;
import com.heg.cvps.repository.CvpsUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final String ASSIGNED_API_KEY = "HEG-CVPS-KEY-TOKEN-2026";
    
    private final CvpsUserRepository userRepository;

    // Inject the User Repository to allow dynamic Oracle DB record lookups
    public SecurityInterceptor(CvpsUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. Check if the request is authenticating via standard X-API-KEY Header (Postman / Frontend UI)
        String providedHeaderKey = request.getHeader("X-API-KEY");
        if (providedHeaderKey != null && providedHeaderKey.equals(ASSIGNED_API_KEY)) {
            return true;
        }

        // 2. Check if the request is coming from a Browser using HTTP Basic Auth (Google Chrome)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String base64Credentials = authHeader.substring("Basic ".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                
                // credentials format is "username:password" (Username expected to be the numeric EMPNO)
                String[] values = credentials.split(":", 2);
                
                if (values.length >= 1) {
                    Long parsedEmpNo = Long.parseLong(values[0].trim());
                    
                    // Dynamic Oracle Verification: Check if the user exists and is active ('Y')
                    Optional<CvpsUser> activeUserOpt = userRepository.findByEmpNoAndActive(parsedEmpNo, "Y");
                    
                    if (activeUserOpt.isPresent()) {
                        return true; // Dynamic match found! Grant access.
                    }
                }
            } catch (Exception e) {
                // Fail silently and fall through to trigger authorization prompt block
            }
        }

        // 3. If no valid dynamic credentials match found, prompt the browser's native login popup box
        response.setHeader("WWW-Authenticate", "Basic realm=\"CVPS Secure Database Document Access\"");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Access Denied. Please provide a valid, active Employee Number (EMPNO) to authenticate.\"}");
        return false;
    }
}