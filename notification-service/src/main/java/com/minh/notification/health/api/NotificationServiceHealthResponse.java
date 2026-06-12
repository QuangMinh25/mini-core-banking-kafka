package com.minh.notification.health.api;

public record NotificationServiceHealthResponse(
		String serviceName,
		String status,
		String kafkaTopic,
		String consumerGroup) {
}
