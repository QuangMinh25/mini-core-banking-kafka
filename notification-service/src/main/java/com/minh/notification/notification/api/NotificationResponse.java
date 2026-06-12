package com.minh.notification.notification.api;

import com.minh.notification.notification.domain.NotificationLogEntity;
import java.math.BigDecimal;

public record NotificationResponse(
		String eventId,
		String referenceNo,
		String fromAccountNo,
		String toAccountNo,
		BigDecimal amount,
		String currency,
		String message,
		String status,
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
