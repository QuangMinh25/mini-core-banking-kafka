package com.minh.bankingcore.outbox;

import com.minh.bankingcore.kafka.TransactionEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherJobTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private OutboxEventFactory outboxEventFactory;

	@Mock
	private TransactionEventProducer transactionEventProducer;

	@Test
	void publishNewEventsShouldMarkEventPublishedOnSuccess() {
		OutboxPublisherJob outboxPublisherJob = new OutboxPublisherJob(
				outboxEventRepository,
				outboxEventFactory,
				transactionEventProducer,
				5
		);
		OutboxEventEntity outboxEvent = new OutboxEventEntity(
				"event-1",
				"TRANSACTION_COMPLETED",
				"TXN001",
				"transaction.completed",
				"{\"referenceNo\":\"TXN001\"}",
				OutboxEventStatus.NEW
		);

		when(outboxEventRepository.findRetryableEvents(anyStatuses(), anyInt(), any(Pageable.class))).thenReturn(List.of(outboxEvent));
		when(outboxEventFactory.resolveMessageKey(outboxEvent)).thenReturn("TXN001");

		outboxPublisherJob.publishNewEvents();

		verify(transactionEventProducer).publish("transaction.completed", "TXN001", "{\"referenceNo\":\"TXN001\"}", "event-1");
		assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
		assertThat(outboxEvent.getPublishedAt()).isNotNull();
		assertThat(outboxEvent.getRetryCount()).isZero();
	}

	@Test
	void publishNewEventsShouldMarkEventFailedAndIncreaseRetryCountOnError() {
		OutboxPublisherJob outboxPublisherJob = new OutboxPublisherJob(
				outboxEventRepository,
				outboxEventFactory,
				transactionEventProducer,
				5
		);
		OutboxEventEntity outboxEvent = new OutboxEventEntity(
				"event-2",
				"TRANSACTION_COMPLETED",
				"TXN002",
				"transaction.completed",
				"{\"referenceNo\":\"TXN002\"}",
				OutboxEventStatus.FAILED
		);

		when(outboxEventRepository.findRetryableEvents(anyStatuses(), anyInt(), any(Pageable.class))).thenReturn(List.of(outboxEvent));
		when(outboxEventFactory.resolveMessageKey(outboxEvent)).thenReturn("TXN002");
		doThrow(new IllegalStateException("Kafka publish failed"))
				.when(transactionEventProducer)
				.publish("transaction.completed", "TXN002", "{\"referenceNo\":\"TXN002\"}", "event-2");

		outboxPublisherJob.publishNewEvents();

		assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
		assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
		assertThat(outboxEvent.getLastError()).contains("Kafka publish failed");
	}

	@SuppressWarnings("unchecked")
	private Collection<OutboxEventStatus> anyStatuses() {
		return any(EnumSet.class);
	}
}
