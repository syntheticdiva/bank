package com.java.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneRequest {
    @NotBlank
    @Pattern(regexp = "^\\+\\d{11}$", message = "Phone format: +79001234567")
    private String phone;
}

