package com.minh.bankingcore.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
		@NotBlank(message = "Source account number is required")
		@Schema(description = "Source account number.", example = "100001")
		String fromAccountNo,
		@NotBlank(message = "Destination account number is required")
		@Schema(description = "Destination account number.", example = "100002")
		String toAccountNo,
		@NotNull(message = "Amount is required")
		@DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero")
		@Schema(description = "Transfer amount.", example = "100000")
		BigDecimal amount,
		@NotBlank(message = "Currency is required")
		@Schema(description = "Transfer currency code.", example = "VND")
		String currency
) {
}
