package com.minh.bankingcore.transaction;

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
@Table(name = "transactions")
public class TransactionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "reference_no", nullable = false, unique = true, length = 64)
	private String referenceNo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransactionType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransactionStatus status;

	@Column(name = "from_account_no", nullable = false, length = 50)
	private String fromAccountNo;

	@Column(name = "to_account_no", nullable = false, length = 50)
	private String toAccountNo;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	protected TransactionEntity() {
	}

	public TransactionEntity(
			String referenceNo,
			TransactionType type,
			TransactionStatus status,
			String fromAccountNo,
			String toAccountNo,
			BigDecimal amount,
			String currency,
			OffsetDateTime createdAt
	) {
		this.referenceNo = referenceNo;
		this.type = type;
		this.status = status;
		this.fromAccountNo = fromAccountNo;
		this.toAccountNo = toAccountNo;
		this.amount = amount;
		this.currency = currency;
		this.createdAt = createdAt;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public TransactionType getType() {
		return type;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public String getToAccountNo() {
		return toAccountNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}
