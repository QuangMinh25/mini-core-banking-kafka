package com.minh.notification.processed_event.api;

import com.minh.notification.processed_event.ProcessedEventEntity;

public record ProcessedEventResponse(
		String eventId,
		String topic,
		String processedAt) {

	public static ProcessedEventResponse from(ProcessedEventEntity entity) {
		return new ProcessedEventResponse(
				entity.getEventId(),
				entity.getTopic(),
				entity.getProcessedAt().toString());
	}
}
