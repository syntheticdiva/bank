package com.java.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailUpdateRequest {
    @NotBlank
    @Email
    private String oldEmail;

    @NotBlank
    @Email
    private String newEmail;
}
