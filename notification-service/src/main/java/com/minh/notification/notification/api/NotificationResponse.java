package com.minh.notification.notification.api;

import io.swagger.v3.oas.annotations.media.Schema;
import com.minh.notification.notification.domain.NotificationLogEntity;
import java.math.BigDecimal;

public record NotificationResponse(
		@Schema(description = "Kafka event id.", example = "evt-20260613-0001")
		String eventId,
		@Schema(description = "Transaction reference number.", example = "TXN-20260613-0001")
		String referenceNo,
		@Schema(description = "Source account number.", example = "100001")
		String fromAccountNo,
		@Schema(description = "Destination account number.", example = "100002")
		String toAccountNo,
		@Schema(description = "Transferred amount.", example = "100000")
		BigDecimal amount,
		@Schema(description = "Transfer currency code.", example = "VND")
		String currency,
		@Schema(description = "Generated notification message.", example = "Transfer TXN-20260613-0001 completed successfully.")
		String message,
		@Schema(description = "Notification processing status.", example = "SENT")
		String status,
		@Schema(description = "Notification creation timestamp.", example = "2026-06-13T09:45:12Z")
		String createdAt) {

	public static NotificationResponse from(NotificationLogEntity entity) {
		return new NotificationResponse(
				entity.getEventId(),
				entity.getReferenceNo(),
				entity.getFromAccountNo(),
				entity.getToAccountNo(),
				entity.getAmount(),
				entity.getCurrency(),
				entity.getMessage(),
				entity.getStatus().name(),
				entity.getCreatedAt().toString());
	}
}
