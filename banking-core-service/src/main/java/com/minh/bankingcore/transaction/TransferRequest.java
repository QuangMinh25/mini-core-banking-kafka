package com.minh.bankingcore.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
		@NotBlank(message = "Source account number is required")
		String fromAccountNo,
		@NotBlank(message = "Destination account number is required")
		String toAccountNo,
		@NotNull(message = "Amount is required")
		@DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero")
		BigDecimal amount,
		@NotBlank(message = "Currency is required")
		String currency
) {
}
