package com.java.bank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Set<String> emails;
    private Set<String> phones;
    private BigDecimal balance;
}
