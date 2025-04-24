package com.java.bank.controller;

import com.java.bank.dto.AuthRequest;
import com.java.bank.dto.AuthResponse;
import com.java.bank.security.JwtUtil;
import com.java.bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 * <p>
 * Обеспечивает endpoints для входа в систему, регистрации обычных пользователей и администраторов.
 * Использует JWT для аутентификации и Spring Security для управления доступом.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Аутентифицирует пользователя и возвращает JWT-токен.
     *
     * @param request DTO с учетными данными (email и пароль)
     * @return ответ с JWT-токеном в формате {@link AuthResponse}
     */
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String token = jwtUtil.generateToken(userDetails);
//        return ResponseEntity.ok(new AuthResponse(token));
//    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

}

