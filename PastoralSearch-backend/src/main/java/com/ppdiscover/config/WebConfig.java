package com.ppdiscover.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web application.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds fairly loose CORS mappings to the registry.
     * 
     * @param registry The CORS registry.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:[*]",
                    "http://frontend:[*]",
                    "http://genesis:[*]",
                        "http://10.0.0.32:[*]"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
