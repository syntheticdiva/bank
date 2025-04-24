package com.java.bank.controller;


import com.java.bank.dto.EmailUpdateRequest;
import com.java.bank.dto.UserDTO;
import com.java.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/search")
    public Page<UserDTO> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @PageableDefault Pageable pageable) {

        return userService.searchUsers(name, dateOfBirth, email, phone, pageable);
    }

    @PutMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public void updateEmail(@RequestParam Long userId, @RequestBody EmailUpdateRequest request) {
        userService.updateEmail(userId, request.getOldEmail(), request.getNewEmail());
    }
}

