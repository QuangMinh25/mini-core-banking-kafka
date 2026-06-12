package com.minh.bankingcore.summary;

import com.minh.bankingcore.transaction.TransactionEntity;
import com.minh.bankingcore.transaction.TransactionRepository;
import com.minh.bankingcore.transaction.TransactionStatus;
import com.minh.bankingcore.transaction.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyTransactionSummaryServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private DailyTransactionSummaryRepository dailyTransactionSummaryRepository;

	@InjectMocks
	private DailyTransactionSummaryService dailyTransactionSummaryService;

	@Test
	void summarizeForDateShouldCreateSummaryWhenMissing() {
		LocalDate summaryDate = LocalDate.of(2026, 6, 11);
		List<TransactionEntity> transactions = List.of(
				new TransactionEntity(
						"TXN001",
						TransactionType.TRANSFER,
						TransactionStatus.SUCCESS,
						"100001",
						"100002",
						new BigDecimal("100000.00"),
						"VND",
						OffsetDateTime.parse("2026-06-11T09:00:00+07:00")
				),
				new TransactionEntity(
						"TXN002",
						TransactionType.TRANSFER,
						TransactionStatus.SUCCESS,
						"100003",
						"100004",
						new BigDecimal("250000.00"),
						"VND",
						OffsetDateTime.parse("2026-06-11T15:45:00+07:00")
				)
		);

		when(transactionRepository.findAllByTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
				eq(TransactionType.TRANSFER),
				any(OffsetDateTime.class),
				any(OffsetDateTime.class)
		)).thenReturn(transactions);
		when(dailyTransactionSummaryRepository.findBySummaryDate(summaryDate)).thenReturn(Optional.empty());
		when(dailyTransactionSummaryRepository.save(any(DailyTransactionSummaryEntity.class)))
				.thenAnswer(invocation -> invocation.getArgument(0, DailyTransactionSummaryEntity.class));

		DailyTransactionSummaryEntity summary = dailyTransactionSummaryService.summarizeForDate(summaryDate);

		assertThat(summary.getSummaryDate()).isEqualTo(summaryDate);
		assertThat(summary.getTotalTransferCount()).isEqualTo(2);
		assertThat(summary.getTotalTransferAmount()).isEqualByComparingTo("350000.00");
		assertThat(summary.getTotalSuccessCount()).isEqualTo(2);
		assertThat(summary.getTotalFailedCount()).isEqualTo(0);
	}

	@Test
	void summarizeForDateShouldUpdateExistingSummaryWithoutCreatingDuplicateRow() {
		LocalDate summaryDate = LocalDate.of(2026, 6, 11);
		DailyTransactionSummaryEntity existingSummary = new DailyTransactionSummaryEntity(
				summaryDate,
				1,
				new BigDecimal("100000.00"),
				1,
				0
		);
		List<TransactionEntity> transactions = List.of(
				new TransactionEntity(
						"TXN003",
						TransactionType.TRANSFER,
						TransactionStatus.SUCCESS,
						"100005",
						"100006",
						new BigDecimal("500000.00"),
						"VND",
						OffsetDateTime.parse("2026-06-11T17:30:00+07:00")
				)
		);

		when(transactionRepository.findAllByTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
				eq(TransactionType.TRANSFER),
				any(OffsetDateTime.class),
				any(OffsetDateTime.class)
		)).thenReturn(transactions);
		when(dailyTransactionSummaryRepository.findBySummaryDate(summaryDate)).thenReturn(Optional.of(existingSummary));
		when(dailyTransactionSummaryRepository.save(existingSummary)).thenReturn(existingSummary);

		DailyTransactionSummaryEntity summary = dailyTransactionSummaryService.summarizeForDate(summaryDate);

		assertThat(summary).isSameAs(existingSummary);
		assertThat(summary.getTotalTransferCount()).isEqualTo(1);
		assertThat(summary.getTotalTransferAmount()).isEqualByComparingTo("500000.00");
		assertThat(summary.getTotalSuccessCount()).isEqualTo(1);
		assertThat(summary.getTotalFailedCount()).isEqualTo(0);

		ArgumentCaptor<DailyTransactionSummaryEntity> summaryCaptor = ArgumentCaptor.forClass(DailyTransactionSummaryEntity.class);
		verify(dailyTransactionSummaryRepository).save(summaryCaptor.capture());
		assertThat(summaryCaptor.getValue()).isSameAs(existingSummary);
	}
}
