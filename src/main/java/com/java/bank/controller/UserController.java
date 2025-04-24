package com.java.bank.controller;


import com.java.bank.dto.*;
import com.java.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API для управления пользователями и их контактами")
public class UserController {
    private final UserService userService;
    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей с фильтрацией")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @Parameter(description = "User name (partial match)", example = "John")
            @RequestParam(required = false) String name,

            @Parameter(description = "Date of birth in format dd.MM.yyyy", example = "15.05.1990")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,

            @Parameter(description = "Email address", example = "user@example.com")
            @RequestParam(required = false) @Email String email,

            @Parameter(description = "Phone number starting with 7 followed by 10 digits", example = "79123456789")
            @RequestParam(required = false) @Pattern(regexp = "^7\\d{10}$") String phone,

            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {

        Page<UserDTO> result = userService.searchUsers(name, dateOfBirth, email, phone, pageable);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> updateEmail(
            @RequestParam Long userId,
            @RequestBody EmailUpdateRequest request) {

        userService.updateEmail(userId, request.getOldEmail(), request.getNewEmail());
        return ResponseEntity.ok(new ApiResponse("Email successfully updated!"));
    }
    @Operation(summary = "Добавить email")
    @PostMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addEmail(
            @RequestParam Long userId,
            @Valid @RequestBody EmailAddRequest request) {
        userService.addEmail(userId, request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Email успешно добавлен"));
    }

    @Operation(summary = "Удалить email")
    @DeleteMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deleteEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.deleteEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Email успешно удален"));
    }
    @Operation(summary = "Установить основной email")
    @PostMapping("/emails/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.setPrimaryEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Основной email успешно обновлен"));
    }
    @Operation(summary = "Добавить телефон")
    @PostMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addPhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneAddRequest request) {
        userService.addPhone(userId, request.getPhone());
        return ResponseEntity.ok(new ApiResponse("Телефон успешно добавлен"));
    }

    @Operation(summary = "Обновить телефон")
    @PutMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> updatePhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneUpdateRequest request) {
        userService.updatePhone(userId, request.getOldPhone(), request.getNewPhone());
        return ResponseEntity.ok(new ApiResponse("Телефон успешно обновлен"));
    }


    @Operation(summary = "Удалить телефон")
    @DeleteMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deletePhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.deletePhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("Телефон успешно удален"));
    }

    @Operation(summary = "Установить основной телефон")
    @PostMapping("/phones/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryPhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.setPrimaryPhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("Основной телефон успешно обновлен"));
    }
}


