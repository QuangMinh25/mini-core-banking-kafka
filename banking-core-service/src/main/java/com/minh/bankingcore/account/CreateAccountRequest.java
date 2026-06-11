package com.minh.bankingcore.account;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "accountNo is required")
        String accountNo,
        @NotBlank(message = "customerName is required")
        String customerName,
        BigDecimal balance,
        @NotBlank(message = "currency is required")
        String currency
) {
}
