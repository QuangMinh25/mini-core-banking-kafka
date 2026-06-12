package com.minh.bankingcore.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

	Optional<TransactionEntity> findByReferenceNo(String referenceNo);

	List<TransactionEntity> findAllByTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
			TransactionType type,
			OffsetDateTime createdAtStart,
			OffsetDateTime createdAtEnd
	);
}
