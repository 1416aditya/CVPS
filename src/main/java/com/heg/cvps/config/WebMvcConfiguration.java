


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