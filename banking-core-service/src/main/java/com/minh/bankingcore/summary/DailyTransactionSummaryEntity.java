package com.minh.bankingcore.summary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
		name = "daily_transaction_summary",
		uniqueConstraints = @UniqueConstraint(name = "uk_daily_transaction_summary_summary_date", columnNames = "summary_date")
)
public class DailyTransactionSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "summary_date", nullable = false)
	private LocalDate summaryDate;

	@Column(name = "total_transfer_count", nullable = false)
	private long totalTransferCount;

	@Column(name = "total_transfer_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalTransferAmount;

	@Column(name = "total_success_count", nullable = false)
	private long totalSuccessCount;

	@Column(name = "total_failed_count", nullable = false)
	private long totalFailedCount;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	protected DailyTransactionSummaryEntity() {
	}

	public DailyTransactionSummaryEntity(
			LocalDate summaryDate,
			long totalTransferCount,
			BigDecimal totalTransferAmount,
			long totalSuccessCount,
			long totalFailedCount
	) {
		this.summaryDate = summaryDate;
		this.totalTransferCount = totalTransferCount;
		this.totalTransferAmount = totalTransferAmount;
		this.totalSuccessCount = totalSuccessCount;
		this.totalFailedCount = totalFailedCount;
	}

	@PrePersist
	void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = OffsetDateTime.now();
	}

	public LocalDate getSummaryDate() {
		return summaryDate;
	}

	public long getTotalTransferCount() {
		return totalTransferCount;
	}

	public BigDecimal getTotalTransferAmount() {
		return totalTransferAmount;
	}

	public long getTotalSuccessCount() {
		return totalSuccessCount;
	}

	public long getTotalFailedCount() {
		return totalFailedCount;
	}

	public void updateTotals(
			long totalTransferCount,
			BigDecimal totalTransferAmount,
			long totalSuccessCount,
			long totalFailedCount
	) {
		this.totalTransferCount = totalTransferCount;
		this.totalTransferAmount = totalTransferAmount;
		this.totalSuccessCount = totalSuccessCount;
		this.totalFailedCount = totalFailedCount;
	}
}
