package com.minh.bankingcore.ledger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, Long> {

	Page<LedgerEntryEntity> findByAccountNoAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
			String accountNo,
			OffsetDateTime fromDateTime,
			OffsetDateTime toDateTime,
			Pageable pageable
	);

	Page<LedgerEntryEntity> findByAccountNoAndCreatedAtGreaterThanEqual(
			String accountNo,
			OffsetDateTime fromDateTime,
			Pageable pageable
	);

	Page<LedgerEntryEntity> findByAccountNoAndCreatedAtLessThanEqual(
			String accountNo,
			OffsetDateTime toDateTime,
			Pageable pageable
	);

	Page<LedgerEntryEntity> findByAccountNo(String accountNo, Pageable pageable);
}
