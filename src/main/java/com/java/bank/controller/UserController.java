package com.java.bank.controller;


import com.java.bank.dto.*;
import com.java.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse> updateEmail(
            @RequestParam Long userId,
            @RequestBody EmailUpdateRequest request) {

        userService.updateEmail(userId, request.getOldEmail(), request.getNewEmail());
        return ResponseEntity.ok(new ApiResponse("Email successfully updated!"));
    }
    @Operation(summary = "Добавить email")
//    @ApiResponse(responseCode = "200", description = "Email успешно добавлен")
    @PostMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addEmail(
            @RequestParam Long userId,
            @Valid @RequestBody EmailAddRequest request) {
        userService.addEmail(userId, request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Email успешно добавлен"));
    }

    @Operation(summary = "Удалить email")
//    @ApiResponse(responseCode = "200", description = "Email успешно удален")
    @DeleteMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deleteEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.deleteEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Email успешно удален"));
    }
    @Operation(summary = "Установить основной email")
//    @ApiResponse(responseCode = "200", description = "Основной email установлен")
    @PostMapping("/emails/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.setPrimaryEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Основной email успешно обновлен"));
    }
    // Phone endpoints
    @Operation(summary = "Добавить телефон")
//    @ApiResponse(responseCode = "200", description = "Телефон успешно добавлен")
    @PostMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addPhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneAddRequest request) {
        userService.addPhone(userId, request.getPhone());
        return ResponseEntity.ok(new ApiResponse("Телефон успешно добавлен"));
    }

    @Operation(summary = "Обновить телефон")
//    @ApiResponse(responseCode = "200", description = "Телефон успешно обновлен")
    @PutMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> updatePhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneUpdateRequest request) {
        userService.updatePhone(userId, request.getOldPhone(), request.getNewPhone());
        return ResponseEntity.ok(new ApiResponse("Телефон успешно обновлен"));
    }


    @Operation(summary = "Удалить телефон")
//    @ApiResponse(responseCode = "200", description = "Телефон успешно удален")
    @DeleteMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deletePhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.deletePhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("Телефон успешно удален"));
    }

    @Operation(summary = "Установить основной телефон")
//    @ApiResponse(responseCode = "200", description = "Основной телефон установлен")
    @PostMapping("/phones/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryPhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.setPrimaryPhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("Основной телефон успешно обновлен"));
    }
}


