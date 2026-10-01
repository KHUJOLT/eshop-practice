package com.eshop.khujolt.eshop_khujolt_backend.controller;

import com.eshop.khujolt.eshop_khujolt_backend.dto.request.LoginRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.request.RegisterRequest;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.LoginResponse;
import com.eshop.khujolt.eshop_khujolt_backend.dto.response.UserResponse;
import com.eshop.khujolt.eshop_khujolt_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
