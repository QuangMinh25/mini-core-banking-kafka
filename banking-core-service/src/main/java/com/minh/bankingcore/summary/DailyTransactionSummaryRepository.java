package com.minh.bankingcore.summary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyTransactionSummaryRepository extends JpaRepository<DailyTransactionSummaryEntity, Long> {

	Optional<DailyTransactionSummaryEntity> findBySummaryDate(LocalDate summaryDate);
}
