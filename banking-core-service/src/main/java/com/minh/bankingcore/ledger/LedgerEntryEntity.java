package com.minh.bankingcore.ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "transaction_reference_no", nullable = false, length = 100)
	private String transactionReferenceNo;

	@Column(name = "account_no", nullable = false, length = 50)
	private String accountNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "entry_type", nullable = false, length = 20)
	private LedgerEntryType entryType;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
	private BigDecimal balanceAfter;

	@Column(nullable = false)
	private String description;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	protected LedgerEntryEntity() {
	}

	public LedgerEntryEntity(
			String transactionReferenceNo,
			String accountNo,
			LedgerEntryType entryType,
			BigDecimal amount,
			BigDecimal balanceAfter,
			String description,
			OffsetDateTime createdAt
	) {
		this.transactionReferenceNo = transactionReferenceNo;
		this.accountNo = accountNo;
		this.entryType = entryType;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.createdAt = createdAt;
	}
}
