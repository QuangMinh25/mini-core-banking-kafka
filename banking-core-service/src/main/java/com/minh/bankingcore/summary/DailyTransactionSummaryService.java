package com.minh.bankingcore.summary;

import com.minh.bankingcore.transaction.TransactionEntity;
import com.minh.bankingcore.transaction.TransactionRepository;
import com.minh.bankingcore.transaction.TransactionStatus;
import com.minh.bankingcore.transaction.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DailyTransactionSummaryService {

	private final TransactionRepository transactionRepository;
	private final DailyTransactionSummaryRepository dailyTransactionSummaryRepository;

	public DailyTransactionSummaryService(
			TransactionRepository transactionRepository,
			DailyTransactionSummaryRepository dailyTransactionSummaryRepository
	) {
		this.transactionRepository = transactionRepository;
		this.dailyTransactionSummaryRepository = dailyTransactionSummaryRepository;
	}

	@Transactional
	public DailyTransactionSummaryEntity summarizeForDate(LocalDate summaryDate) {
		ZoneId zoneId = ZoneId.systemDefault();
		OffsetDateTime startOfDay = summaryDate.atStartOfDay(zoneId).toOffsetDateTime();
		OffsetDateTime startOfNextDay = summaryDate.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

		List<TransactionEntity> transactions = transactionRepository
				.findAllByTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
						TransactionType.TRANSFER,
						startOfDay,
						startOfNextDay
				);

		long totalTransferCount = transactions.size();
		BigDecimal totalTransferAmount = transactions.stream()
				.map(TransactionEntity::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		long totalSuccessCount = transactions.stream()
				.filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCESS)
				.count();
		long totalFailedCount = totalTransferCount - totalSuccessCount;

		DailyTransactionSummaryEntity summary = dailyTransactionSummaryRepository.findBySummaryDate(summaryDate)
				.orElseGet(() -> new DailyTransactionSummaryEntity(summaryDate, 0, BigDecimal.ZERO, 0, 0));

		summary.updateTotals(totalTransferCount, totalTransferAmount, totalSuccessCount, totalFailedCount);
		return dailyTransactionSummaryRepository.save(summary);
	}
}
