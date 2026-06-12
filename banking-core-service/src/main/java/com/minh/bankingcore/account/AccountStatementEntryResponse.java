package com.minh.bankingcore.account;

import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountStatementEntryResponse(
		String transactionReferenceNo,
		String accountNo,
		LedgerEntryType entryType,
		BigDecimal amount,
		BigDecimal balanceAfter,
		String description,
		OffsetDateTime createdAt
) {
	public static AccountStatementEntryResponse from(LedgerEntryEntity entity) {
		return new AccountStatementEntryResponse(
				entity.getTransactionReferenceNo(),
				entity.getAccountNo(),
				entity.getEntryType(),
				entity.getAmount(),
				entity.getBalanceAfter(),
				entity.getDescription(),
				entity.getCreatedAt()
		);
	}
}
