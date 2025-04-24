package com.java.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneUpdateRequest {
    @NotBlank
    @Pattern(regexp = "^\\+\\d{11}$")
    private String oldPhone;

    @NotBlank
    @Pattern(regexp = "^\\+\\d{11}$")
    private String newPhone;
}
