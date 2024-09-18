package com.encora.synth.aitooling.controller;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Creates a new user.", description = "Registers a new user.")
    @SecurityRequirement(name = "BearerAuthentication")
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCreateResponse> create(@RequestBody UserCreateRequest request) {
        return new ResponseEntity<>(userService.create(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Login via email and password.", description = "Returns a valid JWT and Refresh Token.")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        return new ResponseEntity<>(userService.login(userLoginRequest), HttpStatus.OK);
    }

    @Operation(summary = "Refresh current JWT with a refresh token.", description = "Returns a new JWT.")
    @PostMapping("/refresh")
    public ResponseEntity<UserRefreshResponse> refreshToken(HttpServletRequest request) {
        return new ResponseEntity<>(userService.refreshToken(request), HttpStatus.OK);
    }

    @Operation(summary = "Logout", description = "Invalidates the JWT token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        userService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Dummy endpoint to get CSRF token", description = "Endpoint to get CSRF Token")
    @GetMapping("/csrf")
    public ResponseEntity<String> csrf() {
        return ResponseEntity.noContent().build();
    }
}


