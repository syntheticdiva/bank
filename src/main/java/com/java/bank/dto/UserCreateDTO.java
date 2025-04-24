package com.java.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserCreateDTO {
    @NotBlank
    @Size(max = 500)
    private String name;
    @NotNull
    private LocalDate dateOfBirth;
    @NotBlank
    @Size(min = 8, max = 500)
    private String password;
    @NotEmpty
    private Set<@Email String> emails;
    @NotEmpty
    private Set<@Pattern(regexp = "^7\\d{10}$") String> phones;
    @NotNull
    @DecimalMin(value = "0,0", inclusive = false)
    private BigDecimal initialDeposit;


}
