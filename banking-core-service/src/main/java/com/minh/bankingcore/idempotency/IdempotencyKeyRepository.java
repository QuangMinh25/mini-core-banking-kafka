package com.minh.bankingcore.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, Long> {

	Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);
}
