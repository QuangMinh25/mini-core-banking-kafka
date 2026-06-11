package com.minh.notification.notification.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.processed_event.application.ProcessedEventService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionCompletedEventHandlerTest {

	@Mock
	private NotificationService notificationService;

	@Mock
	private ProcessedEventService processedEventService;

	@InjectMocks
	private TransactionCompletedEventHandler transactionCompletedEventHandler;

	@Test
	void shouldIgnoreDuplicateEventWhenAlreadyProcessed() {
		TransactionCompletedEvent event = sampleEvent();
		when(processedEventService.isProcessed(event.eventId())).thenReturn(true);

		transactionCompletedEventHandler.handle(event, "transaction.completed");

		verify(processedEventService, never()).registerIfAbsent(any(), any());
		verify(notificationService, never()).createNotificationLog(any());
	}

	@Test
	void shouldPersistNotificationAndProcessedEventForNewEvent() {
		TransactionCompletedEvent event = sampleEvent();
		when(processedEventService.isProcessed(event.eventId())).thenReturn(false);
		when(processedEventService.registerIfAbsent(event.eventId(), "transaction.completed")).thenReturn(true);

		transactionCompletedEventHandler.handle(event, "transaction.completed");

		verify(processedEventService).registerIfAbsent(event.eventId(), "transaction.completed");
		verify(notificationService).createNotificationLog(eq(event));
	}

	@Test
	void shouldIgnoreDuplicateEventWhenConcurrentRegistrationFails() {
		TransactionCompletedEvent event = sampleEvent();
		when(processedEventService.isProcessed(event.eventId())).thenReturn(false);
		when(processedEventService.registerIfAbsent(event.eventId(), "transaction.completed")).thenReturn(false);

		transactionCompletedEventHandler.handle(event, "transaction.completed");

		verify(notificationService, never()).createNotificationLog(any());
	}

	private TransactionCompletedEvent sampleEvent() {
		return new TransactionCompletedEvent(
				"evt-001",
				"transaction.completed",
				"TXN001",
				"100001",
				"100002",
				new BigDecimal("100000.00"),
				"VND",
				LocalDateTime.now());
	}
}
