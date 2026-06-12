package com.minh.notification.consumer.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

class TransactionCompletedEventDeserializerTest {

	@Test
	void shouldDeserializeTransactionCompletedEventWithJavaTime() {
		JacksonJsonDeserializer<TransactionCompletedEvent> deserializer = new JacksonJsonDeserializer<>();
		deserializer.configure(Map.of(
				JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.minh.notification.consumer.event",
				JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false,
				JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, TransactionCompletedEvent.class.getName()),
				false);

		String json = """
				{
				  "eventId": "evt-001",
				  "eventType": "%s",
				  "referenceNo": "TXN001",
				  "fromAccountNo": "100001",
				  "toAccountNo": "100002",
				  "amount": 100000.00,
				  "currency": "VND",
				  "occurredAt": "2026-06-11T23:45:00"
				}
				""".formatted(TransactionCompletedEvent.EVENT_TYPE);

		TransactionCompletedEvent event = deserializer.deserialize(
				"transaction.completed",
				json.getBytes(StandardCharsets.UTF_8));

		assertEquals("evt-001", event.eventId());
		assertEquals(TransactionCompletedEvent.EVENT_TYPE, event.eventType());
		assertEquals(new BigDecimal("100000.00"), event.amount());
		assertEquals(LocalDateTime.of(2026, 6, 11, 23, 45), event.occurredAt());
	}
}
