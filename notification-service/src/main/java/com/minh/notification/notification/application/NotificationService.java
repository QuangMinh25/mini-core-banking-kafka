package com.minh.notification.notification.application;

import com.minh.notification.consumer.event.TransactionCompletedEvent;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.common.api.ResourceNotFoundException;
import com.minh.notification.notification.api.NotificationResponse;
import com.minh.notification.notification.domain.NotificationLogEntity;
import com.minh.notification.notification.domain.NotificationStatus;
import com.minh.notification.notification.infrastructure.NotificationLogRepository;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

	public PageResponse<NotificationResponse> getNotifications(int page, int size, String status,
			String referenceNo, String eventId) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return PageResponse.from(notificationLogRepository.findAll(
				notificationSpecification(status, referenceNo, eventId), pageRequest)
				.map(NotificationResponse::from));
	}

	public NotificationResponse getNotificationByEventId(@NonNull String eventId) {
		return notificationLogRepository.findFirstByEventIdOrderByCreatedAtDesc(eventId)
				.map(NotificationResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Notification not found for eventId=%s".formatted(eventId)));
	}

	private String buildMessage(TransactionCompletedEvent event) {
		return "Transfer %s from %s to %s completed with amount %s %s"
				.formatted(event.referenceNo(), event.fromAccountNo(), event.toAccountNo(),
						event.amount(), event.currency());
	}

	private Specification<NotificationLogEntity> notificationSpecification(String status, String referenceNo,
			String eventId) {
		return Specification.allOf(
				hasStatus(status),
				hasReferenceNo(referenceNo),
				hasEventId(eventId));
	}

	private Specification<NotificationLogEntity> hasStatus(String status) {
		if (!StringUtils.hasText(status)) {
			return null;
		}

		NotificationStatus notificationStatus = NotificationStatus.valueOf(status.trim().toUpperCase());
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), notificationStatus);
	}

	private Specification<NotificationLogEntity> hasReferenceNo(String referenceNo) {
		if (!StringUtils.hasText(referenceNo)) {
			return null;
		}

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("referenceNo"), referenceNo.trim());
	}

	private Specification<NotificationLogEntity> hasEventId(String eventId) {
		if (!StringUtils.hasText(eventId)) {
			return null;
		}

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("eventId"), eventId.trim());
	}
}
