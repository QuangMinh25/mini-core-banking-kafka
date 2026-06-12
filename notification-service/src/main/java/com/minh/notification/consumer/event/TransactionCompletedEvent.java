package com.minh.notification.consumer.event;

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
	public static final String EVENT_TYPE = "TRANSACTION_COMPLETED";
}
