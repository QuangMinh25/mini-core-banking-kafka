package com.minh.bankingcore.account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountResponse(
        String accountNo,
        String customerName,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static AccountResponse from(AccountEntity entity) {
        return new AccountResponse(
                entity.getAccountNo(),
                entity.getCustomerName(),
                entity.getBalance(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
