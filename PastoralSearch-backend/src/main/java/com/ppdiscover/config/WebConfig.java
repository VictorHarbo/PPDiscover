package com.ppdiscover.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:80",
                    "https://localhost:80",
                    "http://localhost:8080",
                    "http://localhost:9000",
                    "http://localhost:9001",
                    "http://localhost:9002",
                    "http://frontend:80",
                    "http://frontend",
                    "http://genesis:9000",
                    "*"  // Allow all origins for development
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
