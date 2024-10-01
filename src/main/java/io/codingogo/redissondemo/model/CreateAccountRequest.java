package io.codingogo.redissondemo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@lombok.Data
public class CreateAccountRequest {
    @NotNull(message = "Owner name cannot be null")
    private String owner;

    @NotNull(message = "Initial balance cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be non-negative")
    private BigDecimal initialBalance;
}
