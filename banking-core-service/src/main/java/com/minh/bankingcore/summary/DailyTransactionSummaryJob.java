package com.minh.bankingcore.summary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyTransactionSummaryJob {

	private static final Logger log = LoggerFactory.getLogger(DailyTransactionSummaryJob.class);

	private final DailyTransactionSummaryService dailyTransactionSummaryService;

	public DailyTransactionSummaryJob(DailyTransactionSummaryService dailyTransactionSummaryService) {
		this.dailyTransactionSummaryService = dailyTransactionSummaryService;
	}

	@Scheduled(cron = "${app.eod.transaction-summary.cron:0 5 0 * * *}")
	public void summarizePreviousDay() {
		LocalDate summaryDate = LocalDate.now().minusDays(1);
		DailyTransactionSummaryEntity summary = dailyTransactionSummaryService.summarizeForDate(summaryDate);
		log.info(
				"Completed end-of-day transaction summary. summaryDate={}, totalTransferCount={}, totalTransferAmount={}",
				summary.getSummaryDate(),
				summary.getTotalTransferCount(),
				summary.getTotalTransferAmount()
		);
	}
}
