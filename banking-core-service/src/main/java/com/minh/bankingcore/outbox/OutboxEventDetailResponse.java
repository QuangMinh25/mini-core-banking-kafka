package com.minh.bankingcore.outbox;

import java.time.OffsetDateTime;

public record OutboxEventDetailResponse(
        String eventId,
        String topic,
        String eventType,
        String aggregateKey,
        String payload,
        OutboxEventStatus status,
        int retryCount,
        String lastError,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt
) {
    public static OutboxEventDetailResponse from(OutboxEventEntity entity) {
        return new OutboxEventDetailResponse(
                entity.getEventId(),
                entity.getTopic(),
                entity.getEventType(),
                entity.getAggregateKey(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}
