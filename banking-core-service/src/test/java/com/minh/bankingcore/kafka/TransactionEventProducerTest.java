package com.minh.bankingcore.kafka;

import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionEventProducerTest {

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@Mock
	private AuditService auditService;

	@Test
	void publishShouldAuditSuccess() throws Exception {
		RecordMetadata metadata = mock(RecordMetadata.class);
		when(metadata.topic()).thenReturn("transaction.completed");
		when(metadata.partition()).thenReturn(1);
		when(metadata.offset()).thenReturn(15L);
		SendResult<String, String> sendResult = mock(SendResult.class);
		when(sendResult.getRecordMetadata()).thenReturn(metadata);
		CompletableFuture<SendResult<String, String>> future =
				CompletableFuture.completedFuture(sendResult);
		when(kafkaTemplate.send("transaction.completed", "TXN202606112230001", "{\"referenceNo\":\"TXN202606112230001\"}"))
				.thenReturn(future);

		new TransactionEventProducer(kafkaTemplate, auditService)
				.publish(
						"transaction.completed",
						"TXN202606112230001",
						"{\"referenceNo\":\"TXN202606112230001\"}",
						"event-1"
				);

		verify(auditService).logSuccess(eq(AuditAction.PUBLISH_KAFKA_EVENT), eq("KAFKA_TOPIC"), eq("TXN202606112230001"), any(), any());
	}

	@Test
	void publishShouldAuditFailure() {
		CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
		future.completeExceptionally(new IllegalStateException("Kafka unavailable"));
		when(kafkaTemplate.send("transaction.completed", "TXN202606112230001", "{\"referenceNo\":\"TXN202606112230001\"}"))
				.thenReturn(future);

		org.assertj.core.api.Assertions.assertThatThrownBy(
				() -> new TransactionEventProducer(kafkaTemplate, auditService)
						.publish(
								"transaction.completed",
								"TXN202606112230001",
								"{\"referenceNo\":\"TXN202606112230001\"}",
								"event-1"
						)
		).isInstanceOf(IllegalStateException.class);

		verify(auditService).logFailure(eq(AuditAction.PUBLISH_KAFKA_EVENT), eq("KAFKA_TOPIC"), eq("TXN202606112230001"), any(), isNull(), any());
	}
}
