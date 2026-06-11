package com.minh.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaConsumerConfig {

	@Bean
	DefaultErrorHandler kafkaErrorHandler() {
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(
				(record, exception) -> log.warn(
						"Skipping Kafka record after processing failure: topic={}, partition={}, offset={}, error={}",
						record.topic(),
						record.partition(),
						record.offset(),
						exception.getClass().getSimpleName()),
				new FixedBackOff(1000L, 2L));
		errorHandler.addNotRetryableExceptions(DeserializationException.class, SerializationException.class);
		return errorHandler;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
			ConsumerFactory<Object, Object> consumerFactory,
			DefaultErrorHandler kafkaErrorHandler) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(kafkaErrorHandler);
		return factory;
	}
}
