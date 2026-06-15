package com.minh.bankingcore.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false, unique = true, length = 64)
	private String eventId;

	@Column(name = "event_type", nullable = false, length = 100)
	private String eventType;

	@Column(name = "aggregate_key", nullable = false, length = 100)
	private String aggregateKey;

	@Column(name = "topic", nullable = false, length = 255)
	private String topic;

	@Column(name = "payload", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OutboxEventStatus status;

	@Column(name = "retry_count", nullable = false)
	private int retryCount;

	@Column(name = "last_error", length = 1000)
	private String lastError;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "published_at")
	private OffsetDateTime publishedAt;

	protected OutboxEventEntity() {
	}

	public OutboxEventEntity(
			String eventId,
			String eventType,
			String aggregateKey,
			String topic,
			String payload,
			OutboxEventStatus status
	) {
		this.eventId = eventId;
		this.eventType = eventType;
		this.aggregateKey = aggregateKey;
		this.topic = topic;
		this.payload = payload;
		this.status = status;
		this.retryCount = 0;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = OffsetDateTime.now();
	}

	public void markPublished(OffsetDateTime publishedAt) {
		this.status = OutboxEventStatus.PUBLISHED;
		this.publishedAt = publishedAt;
		this.lastError = null;
	}

	public void markFailed(String errorMessage) {
		this.status = OutboxEventStatus.FAILED;
		this.retryCount += 1;
		this.lastError = truncate(errorMessage);
	}

	private String truncate(String errorMessage) {
		if (errorMessage == null || errorMessage.length() <= 1000) {
			return errorMessage;
		}
		return errorMessage.substring(0, 1000);
	}
}
