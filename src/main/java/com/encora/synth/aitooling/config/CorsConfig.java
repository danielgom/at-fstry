package com.encora.synth.aitooling.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedOrigins("http://localhost:4200");
        corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE");
        corsRegistration.allowedHeaders(CorsConfiguration.ALL);
        corsRegistration.allowCredentials(true);
    }
}
