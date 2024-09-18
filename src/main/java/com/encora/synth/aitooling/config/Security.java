package com.encora.synth.aitooling.config;

import com.encora.synth.aitooling.security.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class Security {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final Environment environment;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isTestProfile = environment.acceptsProfiles(Profiles.of("test"));

        return http
                .headers(headers -> headers.xssProtection(xss ->
                                xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(cps -> cps.policyDirectives("default-src 'self'")))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsRegistration = new CorsConfiguration();
                    corsRegistration.setAllowedOrigins(List.of("http://localhost:4200"));
                    corsRegistration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    corsRegistration.setAllowCredentials(true);
                    corsRegistration.setAllowedHeaders(List.of("*"));
                    corsRegistration.setExposedHeaders(List.of("Set-Cookie"));
                    return corsRegistration;
                }))
                // Conditionally disable or enable CSRF based on the profile
                .csrf(csrf -> {
                    if (isTestProfile) {
                        csrf.disable();
                    } else {
                        // Set CSRF cookie repository with HttpOnly false and SameSite=None without Secure for local development
                        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                        csrfTokenRepository.setCookiePath("/");
                        csrfTokenRepository.setCookieName("XSRF-TOKEN");
                        csrf.csrfTokenRepository(csrfTokenRepository)
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
                    }
                })
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers(jwtAuthenticationFilter.unprotectedPaths)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
