package com.minh.notification.consumer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.notification.application.TransactionCompletedEventHandler;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionCompletedConsumerTest {

	@Mock
	private TransactionCompletedEventHandler transactionCompletedEventHandler;

	@InjectMocks
	private TransactionCompletedConsumer consumer;

	@Test
	void shouldConsumeValidEvent() {
		TransactionCompletedEvent event = sampleEvent("evt-001", "TXN001", new BigDecimal("100000"));

		consumer.consume(event, "transaction.completed");

		verify(transactionCompletedEventHandler).handle(event, "transaction.completed");
	}

	@Test
	void shouldIgnoreEventWithoutEventId() {
		TransactionCompletedEvent event = sampleEvent(null, "TXN001", new BigDecimal("100000"));

		consumer.consume(event, "transaction.completed");

		verify(transactionCompletedEventHandler, never()).handle(event, "transaction.completed");
	}

	@Test
	void shouldIgnoreEventWithoutReferenceNo() {
		TransactionCompletedEvent event = sampleEvent("evt-001", null, new BigDecimal("100000"));

		consumer.consume(event, "transaction.completed");

		verify(transactionCompletedEventHandler, never()).handle(event, "transaction.completed");
	}

	@Test
	void shouldIgnoreEventWithoutAmount() {
		TransactionCompletedEvent event = sampleEvent("evt-001", "TXN001", null);

		consumer.consume(event, "transaction.completed");

		verify(transactionCompletedEventHandler, never()).handle(event, "transaction.completed");
	}

	@Test
	void shouldIgnoreNullEvent() {
		consumer.consume(null, "transaction.completed");

		verify(transactionCompletedEventHandler, never()).handle(null, "transaction.completed");
	}

	private TransactionCompletedEvent sampleEvent(String eventId, String referenceNo, BigDecimal amount) {
		return new TransactionCompletedEvent(
				eventId,
				TransactionCompletedEvent.EVENT_TYPE,
				referenceNo,
				"100001",
				"100002",
				amount,
				"VND",
				LocalDateTime.of(2026, 6, 11, 22, 30));
	}
}
