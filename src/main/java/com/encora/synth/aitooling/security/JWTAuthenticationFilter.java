package com.encora.synth.aitooling.security;

import com.encora.synth.aitooling.dto.Error;
import com.encora.synth.aitooling.dto.UserGetResponse;
import com.encora.synth.aitooling.repository.JWTBlackListRepository;
import com.encora.synth.aitooling.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider JWTProvider;

    private final UserService userService;

    private final JWTBlackListRepository jwtBlackListRepository;

    private final ObjectMapper objectMapper;

    private static final String BEARER_PREFIX = "Bearer ";

    public final String[] unprotectedPaths = {
            "/api/auth/csrf",
            "/api/auth/signup",
            "/api/auth/login",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/v3/api-docs.json",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/swagger-ui/**",
            "/api-docs.yaml",
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui/favicon-16x16.png",
            "/v3/api-docs/swagger-config"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Skip if unprotected path
        if (Set.of(unprotectedPaths).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = this.getJwtFromRequest(request);
        if (shouldSetAuth(request, jwt)) {
            String email = JWTProvider.getSubjectFromJWT(jwt);

            UserGetResponse user = userService.getByEmail(email);

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
                    null, null));

            filterChain.doFilter(request, response);
        } else {
            HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
            Error error = Error.builder()
                    .path(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .error(unauthorized.name())
                    .status(unauthorized.value())
                    .reason("JWT is invalid, is empty or has already expired")
                    .build();

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(unauthorized.value());
            response.getWriter().write(objectMapper.writeValueAsString(error));
            response.flushBuffer();
        }
    }

    private boolean shouldSetAuth(HttpServletRequest request, String jwt) {
        return (StringUtils.hasText(jwt) && (JWTProvider.isValidToken(jwt)) ||
                request.getRequestURI().equals("/api/auth/refresh")) &&
                jwtBlackListRepository.findByExpiredToken(jwt).isEmpty();
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace(BEARER_PREFIX, "");
        }
        return bearerToken;
    }
}
