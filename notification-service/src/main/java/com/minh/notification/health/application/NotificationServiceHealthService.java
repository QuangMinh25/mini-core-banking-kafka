package com.minh.notification.health.application;

import com.minh.notification.config.KafkaTopicProperties;
import com.minh.notification.health.api.NotificationServiceHealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceHealthService {

	private final KafkaTopicProperties kafkaTopicProperties;

	@Value("${spring.application.name}")
	private String serviceName;

	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroup;

	public NotificationServiceHealthResponse getHealth() {
		return new NotificationServiceHealthResponse(
				serviceName,
				"UP",
				kafkaTopicProperties.transactionCompleted(),
				consumerGroup);
	}
}
