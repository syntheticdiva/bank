package com.java.bank.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneAddRequest {
    @Pattern(regexp = "^7\\d{10}$", message = "Incorrect phone format")
    private String phone;
}