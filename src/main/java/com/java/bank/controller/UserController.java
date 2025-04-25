package com.java.bank.controller;

import com.java.bank.dto.*;
import com.java.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API for managing users and their contacts")
public class UserController {
    private final UserService userService;
    @GetMapping("/search")
    @Operation(summary = "Search for users with filtering")
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
    @Operation(summary = "Update email")
    @PutMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    @Caching(
            evict = {
                    @CacheEvict(value = "users", key = "'user_with_emails:' + #userId"),
                    @CacheEvict(value = "users", key = "'user_full:' + #userId"),
                    @CacheEvict(value = "emails", key = "'email_exists:' + #request.oldEmail"),
                    @CacheEvict(value = "emails", key = "'email_exists:' + #request.newEmail")
            }
    )
    public ResponseEntity<ApiResponse> updateEmail(
            @RequestParam Long userId,
            @RequestBody EmailUpdateRequest request) {

        userService.updateEmail(userId, request.getOldEmail(), request.getNewEmail());
        return ResponseEntity.ok(new ApiResponse("Email successfully updated!"));
    }
    @Operation(summary = "Add email")
    @PostMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addEmail(
            @RequestParam Long userId,
            @Valid @RequestBody EmailAddRequest request) {
        userService.addEmail(userId, request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Email added successfully"));
    }

    @Operation(summary = "Delete email")
    @DeleteMapping("/emails")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deleteEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.deleteEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Email successfully deleted"));
    }
    @Operation(summary = "Set primary email")
    @PostMapping("/emails/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        userService.setPrimaryEmail(userId, email);
        return ResponseEntity.ok(new ApiResponse("Primary email updated successfully"));
    }
    @Operation(summary = "Add phone")
    @PostMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> addPhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneAddRequest request) {
        userService.addPhone(userId, request.getPhone());
        return ResponseEntity.ok(new ApiResponse("Phone added successfully"));
    }

    @Operation(summary = "Update phone")
    @PutMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> updatePhone(
            @RequestParam Long userId,
            @Valid @RequestBody PhoneUpdateRequest request) {
        userService.updatePhone(userId, request.getOldPhone(), request.getNewPhone());
        return ResponseEntity.ok(new ApiResponse("The phone has been updated successfully"));
    }


    @Operation(summary = "Delete phone")
    @DeleteMapping("/phones")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> deletePhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.deletePhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("Phone successfully removed"));
    }

    @Operation(summary = "Set primary phone")
    @PostMapping("/phones/primary")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ApiResponse> setPrimaryPhone(
            @RequestParam Long userId,
            @RequestParam String phone) {
        userService.setPrimaryPhone(userId, phone);
        return ResponseEntity.ok(new ApiResponse("The main phone has been updated successfully"));
    }
}


