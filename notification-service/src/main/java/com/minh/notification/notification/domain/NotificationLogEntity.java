package com.minh.notification.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notification_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false, length = 100)
	private String eventId;

	@Column(name = "reference_no", nullable = false, length = 50)
	private String referenceNo;

	@Column(name = "from_account_no", length = 30)
	private String fromAccountNo;

	@Column(name = "to_account_no", length = 30)
	private String toAccountNo;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 10)
	private String currency;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private NotificationStatus status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	private NotificationLogEntity(Long id, String eventId, String referenceNo, String fromAccountNo,
			String toAccountNo, BigDecimal amount, String currency, String message,
			NotificationStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.eventId = eventId;
		this.referenceNo = referenceNo;
		this.fromAccountNo = fromAccountNo;
		this.toAccountNo = toAccountNo;
		this.amount = amount;
		this.currency = currency;
		this.message = message;
		this.status = status;
		this.createdAt = createdAt;
	}
}
