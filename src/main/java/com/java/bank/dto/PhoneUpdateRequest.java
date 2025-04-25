package com.java.bank.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneUpdateRequest {
    @Pattern(regexp = "^7\\d{10}$", message = "Invalid phone format. Example: 79123456789")
    private String oldPhone;

    @Pattern(regexp = "^7\\d{10}$", message = "Invalid phone format. Example: 79123456789")
    private String newPhone;
}