package com.java.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull
    @Positive
    private Long toUserId;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;


}



