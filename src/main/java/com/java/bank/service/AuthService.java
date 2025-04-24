//package com.java.bank.service;
//
//import com.java.bank.entity.User;
//import com.java.bank.exception.AuthenticationException;
//import com.java.bank.repository.UserRepository;
//import com.java.bank.security.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    public String authenticate(String login, String password) {
//        User user = userRepository.findByEmailOrPhone(login, login)
//                .orElseThrow(() -> new AuthenticationException("User not found"));
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new AuthenticationException("Invalid password");
//        }
//
//        return jwtUtil.generateToken(user.getId());
//    }
//}
