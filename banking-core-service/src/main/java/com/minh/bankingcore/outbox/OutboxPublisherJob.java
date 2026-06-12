package com.minh.bankingcore.outbox;

import com.minh.bankingcore.kafka.TransactionEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

@Component
public class OutboxPublisherJob {

	private static final Logger log = LoggerFactory.getLogger(OutboxPublisherJob.class);
	private static final int BATCH_SIZE = 100;

	private final OutboxEventRepository outboxEventRepository;
	private final OutboxEventFactory outboxEventFactory;
	private final TransactionEventProducer transactionEventProducer;
	private final int retryLimit;

	public OutboxPublisherJob(
			OutboxEventRepository outboxEventRepository,
			OutboxEventFactory outboxEventFactory,
			TransactionEventProducer transactionEventProducer,
			@Value("${app.outbox.publisher.retry-limit:5}") int retryLimit
	) {
		this.outboxEventRepository = outboxEventRepository;
		this.outboxEventFactory = outboxEventFactory;
		this.transactionEventProducer = transactionEventProducer;
		this.retryLimit = retryLimit;
	}

	@Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay-ms:5000}")
	@Transactional
	public void publishNewEvents() {
		List<OutboxEventEntity> newEvents = outboxEventRepository.findRetryableEvents(
				EnumSet.of(OutboxEventStatus.NEW, OutboxEventStatus.FAILED),
				retryLimit,
				PageRequest.of(0, BATCH_SIZE)
		);
		for (OutboxEventEntity outboxEvent : newEvents) {
			publish(outboxEvent);
		}
	}

	private void publish(OutboxEventEntity outboxEvent) {
		String messageKey = outboxEventFactory.resolveMessageKey(outboxEvent);
		try {
			transactionEventProducer.publish(
					outboxEvent.getTopic(),
					messageKey,
					outboxEvent.getPayload(),
					outboxEvent.getEventId()
			);
			outboxEvent.markPublished(OffsetDateTime.now());
			log.info(
					"Published outbox event successfully. eventId={}, topic={}, key={}",
					outboxEvent.getEventId(),
					outboxEvent.getTopic(),
					messageKey
			);
		} catch (Exception exception) {
			outboxEvent.markFailed(exception.getMessage());
			log.warn(
					"Failed to publish outbox event. eventId={}, topic={}, key={}, retryCount={}",
					outboxEvent.getEventId(),
					outboxEvent.getTopic(),
					messageKey,
					outboxEvent.getRetryCount(),
					exception
			);
		}
	}
}
