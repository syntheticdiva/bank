package com.java.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response format")
public class ApiResponse {
    @Schema(description = "Response message that provides information about the operation result",
            example = "Operation completed successfully")
    private String message;
}