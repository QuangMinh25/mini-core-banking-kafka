package com.minh.bankingcore.kafka;

import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class TransactionEventProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final AuditService auditService;

	public TransactionEventProducer(
			KafkaTemplate<String, String> kafkaTemplate,
			AuditService auditService
	) {
		this.kafkaTemplate = kafkaTemplate;
		this.auditService = auditService;
	}

	public void publish(String topic, String messageKey, String payload, String eventId) {
		Map<String, Object> requestData = requestData(topic, messageKey, eventId);
		try {
			var sendResult = kafkaTemplate.send(topic, messageKey, payload).get();
			auditService.logSuccess(
					AuditAction.PUBLISH_KAFKA_EVENT,
					"KAFKA_TOPIC",
					messageKey,
					requestData,
					Map.of(
							"topic", sendResult.getRecordMetadata().topic(),
							"partition", sendResult.getRecordMetadata().partition(),
							"offset", sendResult.getRecordMetadata().offset()
					)
			);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			auditService.logFailure(AuditAction.PUBLISH_KAFKA_EVENT, "KAFKA_TOPIC", messageKey, requestData, null, exception);
			throw new IllegalStateException("Interrupted while publishing outbox event", exception);
		} catch (ExecutionException exception) {
			auditService.logFailure(AuditAction.PUBLISH_KAFKA_EVENT, "KAFKA_TOPIC", messageKey, requestData, null, exception);
			throw new IllegalStateException("Failed to publish outbox event", exception);
		} catch (RuntimeException exception) {
			auditService.logFailure(AuditAction.PUBLISH_KAFKA_EVENT, "KAFKA_TOPIC", messageKey, requestData, null, exception);
			throw exception;
		}
	}

	private Map<String, Object> requestData(String topic, String messageKey, String eventId) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("topic", topic);
		payload.put("key", messageKey);
		payload.put("eventId", eventId);
		return payload;
	}
}
