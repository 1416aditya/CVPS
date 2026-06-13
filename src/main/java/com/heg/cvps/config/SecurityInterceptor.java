




// package com.heg.cvps.config;

// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;

// @Component
// public class SecurityInterceptor implements HandlerInterceptor {

//     private static final String ASSIGNED_API_KEY = "HEG-CVPS-KEY-TOKEN-2026";

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         // 1. Check for standard X-API-KEY Header (Used by Postman)
//         String providedHeaderKey = request.getHeader("X-API-KEY");
//         if (providedHeaderKey != null && providedHeaderKey.equals(ASSIGNED_API_KEY)) {
//             // Flag session as authenticated for browser downloads
//             request.getSession(true).setAttribute("authenticated", true);
//             return true;
//         }

//         // 2. Check for active Browser Session Cookie (Used by Chrome)
//         HttpSession session = request.getSession(false);
//         if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
//             return true;
//         }

//         response.setStatus(HttpStatus.UNAUTHORIZED.value());
//         response.setContentType("application/json");
//         response.getWriter().write("{\"error\": \"Unauthorized Request\", \"message\": \"Access Denied. Authenticate via Postman first.\"}");
//         return false; 
//     }
// }




package com.heg.cvps.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final String ASSIGNED_API_KEY = "HEG-CVPS-KEY-TOKEN-2026";
    
    // Define a secure username and password your team can use inside Chrome
    private static final String BROWSER_USER = "heg_admin";
    private static final String BROWSER_PASS = "baann_2026";

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
                // credentials format is "username:password"
                String[] values = credentials.split(":", 2);
                
                if (values.length == 2 && BROWSER_USER.equals(values[0]) && BROWSER_PASS.equals(values[1])) {
                    return true; // Credentials match! Let Chrome download the file.
                }
            } catch (Exception e) {
                // Invalid base64 encoding, fall through to trigger login box
            }
        }

        // 3. If no valid credentials found, tell Chrome to open its native login popup box
        response.setHeader("WWW-Authenticate", "Basic realm=\"CVPS Secure Document Download\"");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Please provide valid credentials to download this document.\"}");
        return false;
    }
}