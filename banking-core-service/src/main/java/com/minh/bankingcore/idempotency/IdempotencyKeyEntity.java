package com.minh.bankingcore.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
	private String idempotencyKey;

	@Column(name = "request_hash", nullable = false, length = 64)
	private String requestHash;

	@Column(name = "response_body", columnDefinition = "TEXT")
	private String responseBody;

	@Column(nullable = false, length = 32)
	private String status;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	protected IdempotencyKeyEntity() {
	}

	public IdempotencyKeyEntity(
			String idempotencyKey,
			String requestHash,
			String responseBody,
			String status,
			OffsetDateTime createdAt,
			OffsetDateTime updatedAt
	) {
		this.idempotencyKey = idempotencyKey;
		this.requestHash = requestHash;
		this.responseBody = responseBody;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getRequestHash() {
		return requestHash;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public String getStatus() {
		return status;
	}

	public void complete(String responseBody, String status) {
		this.responseBody = responseBody;
		this.status = status;
		this.updatedAt = OffsetDateTime.now();
	}
}
