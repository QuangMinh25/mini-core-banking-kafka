package com.minh.bankingcore.outbox;

import java.time.OffsetDateTime;

public record OutboxEventResponse(
        String eventId,
        String topic,
        String eventType,
        OutboxEventStatus status,
        int retryCount,
        String lastError,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt
) {
    public static OutboxEventResponse from(OutboxEventEntity entity) {
        return new OutboxEventResponse(
                entity.getEventId(),
                entity.getTopic(),
                entity.getEventType(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}
