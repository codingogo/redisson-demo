package io.codingogo.redissondemo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@lombok.Data
public class WithdrawRequest {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Withdrawal amount must be positive")
    private BigDecimal amount;
}
