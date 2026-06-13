package com.minh.bankingcore.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "accountNo is required")
        @Schema(description = "Unique account number.", example = "100001")
        String accountNo,
        @NotBlank(message = "customerName is required")
        @Schema(description = "Account holder full name.", example = "Nguyen Van A")
        String customerName,
        @Schema(description = "Opening balance.", example = "1000000")
        BigDecimal balance,
        @NotBlank(message = "currency is required")
        @Schema(description = "Account currency code.", example = "VND")
        String currency
) {
}
