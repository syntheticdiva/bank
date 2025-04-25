package com.java.bank.controller;

import com.java.bank.dto.AuthRequest;
import com.java.bank.dto.AuthResponse;
import com.java.bank.entity.User;
import com.java.bank.security.JwtUtil;
import com.java.bank.service.CustomUserDetailsService;
import com.java.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        User user = (User) userDetails;

        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}

