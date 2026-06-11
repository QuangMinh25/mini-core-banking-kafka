package com.minh.notification.notification.application;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.notification.domain.NotificationLogEntity;
import com.minh.notification.notification.domain.NotificationStatus;
import com.minh.notification.notification.infrastructure.NotificationLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationLogRepository notificationLogRepository;

	public NotificationLogEntity createNotificationLog(TransactionCompletedEvent event) {
		NotificationLogEntity notificationLog = NotificationLogEntity.builder()
				.eventId(event.eventId())
				.referenceNo(event.referenceNo())
				.fromAccountNo(event.fromAccountNo())
				.toAccountNo(event.toAccountNo())
				.amount(event.amount())
				.currency(event.currency())
				.status(NotificationStatus.CREATED)
				.message(buildMessage(event))
				.createdAt(LocalDateTime.now())
				.build();

		return notificationLogRepository.save(notificationLog);
	}

	private String buildMessage(TransactionCompletedEvent event) {
		return "Transfer %s from %s to %s completed with amount %s %s"
				.formatted(event.referenceNo(), event.fromAccountNo(), event.toAccountNo(),
						event.amount(), event.currency());
	}
}
