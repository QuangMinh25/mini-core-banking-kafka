package com.minh.bankingcore.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.bankingcore.kafka.TransactionCompletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventFactory {

	private final ObjectMapper objectMapper;
	private final String transactionCompletedTopic;

	public OutboxEventFactory(
			ObjectMapper objectMapper,
			@Value("${app.kafka.topics.transaction-completed}") String transactionCompletedTopic
	) {
		this.objectMapper = objectMapper;
		this.transactionCompletedTopic = transactionCompletedTopic;
	}

	public OutboxEventEntity createTransactionCompletedEvent(TransactionCompletedEvent event) {
		return new OutboxEventEntity(
				event.eventId(),
				event.eventType(),
				event.referenceNo(),
				transactionCompletedTopic,
				serialize(event),
				OutboxEventStatus.NEW
		);
	}

	public String resolveMessageKey(OutboxEventEntity event) {
		try {
			TransactionCompletedEvent payload = objectMapper.readValue(event.getPayload(), TransactionCompletedEvent.class);
			if (payload.referenceNo() != null && !payload.referenceNo().isBlank()) {
				return payload.referenceNo();
			}
			return event.getEventId();
		} catch (JsonProcessingException exception) {
			return event.getEventId();
		}
	}

	private String serialize(TransactionCompletedEvent event) {
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Failed to serialize outbox event payload", exception);
		}
	}
}
