package com.minh.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicProperties(String transactionCompleted, String transactionCompletedDlt) {
}
