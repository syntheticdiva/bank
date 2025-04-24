package com.java.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateDTO {
    @Size(min = 8, max = 500)
    private String password;
    private Set<@Email String > emailsToAdd;
    private Set<String> emailsToRemove;
    private Set<@Pattern(regexp = "^7\\d{10}$") String> phonesToAdd;
    private Set<String> phonesToRemove;
}
