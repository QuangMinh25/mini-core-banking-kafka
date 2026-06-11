package com.minh.bankingcore.transaction;

import java.math.BigDecimal;

public record TransferResponse(
		String referenceNo,
		String status,
		String fromAccountNo,
		String toAccountNo,
		BigDecimal amount,
		String currency
) {
}
