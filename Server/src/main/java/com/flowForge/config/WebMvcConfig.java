package com.flowForge.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**")
                .allowedMethods("*")
                .allowedOrigins("http://localhost:5173", "https://localhost:5173")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }

}
