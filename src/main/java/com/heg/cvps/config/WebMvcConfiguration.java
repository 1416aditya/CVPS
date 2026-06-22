// package com.heg.cvps.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebMvcConfiguration implements WebMvcConfigurer {

//     private final SecurityInterceptor securityInterceptor;

//     public WebMvcConfiguration(SecurityInterceptor securityInterceptor) {
//         this.securityInterceptor = securityInterceptor;
//     }

//     @Override
//     public void addInterceptors(InterceptorRegistry registry) {
//         // Apply the API Key security check to all version 1 endpoints
//         registry.addInterceptor(securityInterceptor).addPathPatterns("/api/v1/**");
//     }

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         // Allows full local network access to prevent cross-origin browser blocking blocks
//         registry.addMapping("/**")
//                 .allowedOrigins("*")
//                 .allowedMethods("GET", "POST")
//                 .allowedHeaders("*");
//     }
// }


package com.heg.cvps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final SecurityInterceptor interceptor;

    public WebMvcConfiguration(SecurityInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 🚫 SECURITY DISABLED FOR FRONTEND PARTNER TESTING:
        // Hame is line ko comment out karna hai taaki interceptor active na ho.
        // registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}