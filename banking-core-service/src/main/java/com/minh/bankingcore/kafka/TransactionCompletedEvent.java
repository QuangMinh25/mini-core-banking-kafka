package com.minh.bankingcore.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCompletedEvent(
		String eventId,
		String eventType,
		String referenceNo,
		String fromAccountNo,
		String toAccountNo,
		BigDecimal amount,
		String currency,
		LocalDateTime occurredAt
) {
}
