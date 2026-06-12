package com.minh.bankingcore.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
        String referenceNo,
        String fromAccountNo,
        String toAccountNo,
        BigDecimal amount,
        String currency,
        TransactionType type,
        TransactionStatus status,
        OffsetDateTime createdAt
) {
    public static TransactionResponse from(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getReferenceNo(),
                entity.getFromAccountNo(),
                entity.getToAccountNo(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getType(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
