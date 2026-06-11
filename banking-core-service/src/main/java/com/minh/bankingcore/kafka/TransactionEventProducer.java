package com.minh.bankingcore.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer {

	private final KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate;
	private final String transactionCompletedTopic;

	public TransactionEventProducer(
			KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate,
			@Value("${app.kafka.topics.transaction-completed}") String transactionCompletedTopic
	) {
		this.kafkaTemplate = kafkaTemplate;
		this.transactionCompletedTopic = transactionCompletedTopic;
	}

	public void publish(TransactionCompletedEvent event) {
		kafkaTemplate.send(transactionCompletedTopic, event.referenceNo(), event);
	}
}
