package com.minh.bankingcore.audit;

import java.time.OffsetDateTime;

public record AuditLogResponse(
        String action,
        String resourceType,
        String resourceId,
        String requestData,
        String responseData,
        String status,
        String errorMessage,
        OffsetDateTime createdAt
) {
    public static AuditLogResponse from(AuditLogEntity entity) {
        return new AuditLogResponse(
                entity.getAction(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getRequestData(),
                entity.getResponseData(),
                entity.getStatus(),
                entity.getErrorMessage(),
                entity.getCreatedAt()
        );
    }
}
