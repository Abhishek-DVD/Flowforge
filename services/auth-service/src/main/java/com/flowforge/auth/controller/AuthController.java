package com.flowforge.auth.controller;

import com.flowforge.auth.dto.AuthResponse;
import com.flowforge.auth.dto.LoginRequest;
import com.flowforge.auth.dto.RegisterRequest;
import com.flowforge.auth.dto.RegisterResponse;
import com.flowforge.auth.dto.UserProfileResponse;
import com.flowforge.auth.entity.User;
import com.flowforge.auth.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/test")
    public String test() {
        return "JWT authentication successful!";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "USER access successful!";
    }

    @GetMapping("/admin/test")
    public String adminTest() {
        return "ADMIN access successful!";
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                authService.getProfile(authentication.getName()));
    }
}