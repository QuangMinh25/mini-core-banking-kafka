package com.minh.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.DeserializationException;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaConsumerConfig {

	@Bean
	DefaultErrorHandler kafkaErrorHandler(
			KafkaOperations<Object, Object> kafkaOperations,
			KafkaTopicProperties kafkaTopicProperties) {
		DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(
				kafkaOperations,
				(record, exception) -> new TopicPartition(
						kafkaTopicProperties.transactionCompletedDlt(),
						record.partition()));

		ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(2);
		backOff.setInitialInterval(1000L);
		backOff.setMultiplier(1.0);
		backOff.setMaxInterval(1000L);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(
				(record, exception) -> {
					log.error(
							"Publishing Kafka record to DLT after retries exhausted: sourceTopic={}, dltTopic={}, partition={}, offset={}, error={}",
							record.topic(),
							kafkaTopicProperties.transactionCompletedDlt(),
							record.partition(),
							record.offset(),
							exception.getClass().getSimpleName(),
							exception);
					deadLetterPublishingRecoverer.accept(record, exception);
				},
				backOff);
		errorHandler.addNotRetryableExceptions(DeserializationException.class, SerializationException.class);
		errorHandler.setRetryListeners(new RetryListener() {
			@Override
			public void failedDelivery(org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
					Exception exception, int deliveryAttempt) {
				log.warn(
						"Retrying Kafka record: topic={}, partition={}, offset={}, attempt={}, error={}",
						record.topic(),
						record.partition(),
						record.offset(),
						deliveryAttempt,
						exception.getClass().getSimpleName());
			}

			@Override
			public void recovered(org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record, Exception exception) {
				log.warn(
						"Kafka record recovered to DLT: sourceTopic={}, dltTopic={}, partition={}, offset={}, error={}",
						record.topic(),
						kafkaTopicProperties.transactionCompletedDlt(),
						record.partition(),
						record.offset(),
						exception.getClass().getSimpleName());
			}
		});
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
