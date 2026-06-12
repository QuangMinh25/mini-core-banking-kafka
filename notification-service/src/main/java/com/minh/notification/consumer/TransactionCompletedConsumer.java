package com.minh.notification.consumer;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.notification.application.TransactionCompletedEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCompletedConsumer {

	private final TransactionCompletedEventHandler transactionCompletedEventHandler;

	@KafkaListener(
			topics = "${app.kafka.topics.transaction-completed}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	public void consume(TransactionCompletedEvent event,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		if (event == null || event.eventId() == null || event.referenceNo() == null || event.amount() == null) {
			log.warn(
					"Ignoring invalid transaction.completed event: topic={}, eventId={}, referenceNo={}",
					topic,
					event != null ? event.eventId() : null,
					event != null ? event.referenceNo() : null
			);
			return;
		}

		log.info("Received transaction.completed event with eventId={}", event.eventId());
		transactionCompletedEventHandler.handle(event, topic);
	}
}
