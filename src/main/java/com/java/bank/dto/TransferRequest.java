package com.java.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull @Positive Long toUserId,
        @NotNull @Positive BigDecimal amount
) {}




