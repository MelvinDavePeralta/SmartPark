package com.smart.park.service;

import com.smart.park.dto.LoginRequest;
import com.smart.park.dto.LoginResponse;
import com.smart.park.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${smartpark.auth.username}")
    private String username;

    @Value("${smartpark.auth.password}")
    private String password;

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        if (!username.equals(request.getUsername()) || !password.equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String token = jwtService.generateToken(request.getUsername());

        return new LoginResponse(token);
    }
}