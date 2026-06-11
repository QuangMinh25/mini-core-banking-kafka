package com.minh.notification.notification.application;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.processed_event.application.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionCompletedEventHandler {

	private final NotificationService notificationService;
	private final ProcessedEventService processedEventService;

	@Transactional
	public void handle(TransactionCompletedEvent event, String topic) {
		if (processedEventService.isProcessed(event.eventId())) {
			log.info("Duplicate transaction.completed event ignored for eventId={}", event.eventId());
			return;
		}

		if (!processedEventService.registerIfAbsent(event.eventId(), topic)) {
			log.info("Duplicate transaction.completed event ignored during registration for eventId={}",
					event.eventId());
			return;
		}

		notificationService.createNotificationLog(event);
	}
}
