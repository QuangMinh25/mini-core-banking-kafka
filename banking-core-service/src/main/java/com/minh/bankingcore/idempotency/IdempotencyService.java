package com.minh.bankingcore.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.transaction.TransferRequest;
import com.minh.bankingcore.transaction.TransferResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.function.Supplier;

@Service
public class IdempotencyService {

	private static final String PROCESSING = "PROCESSING";

	private final IdempotencyKeyRepository idempotencyKeyRepository;
	private final ObjectMapper objectMapper;

	public IdempotencyService(IdempotencyKeyRepository idempotencyKeyRepository, ObjectMapper objectMapper) {
		this.idempotencyKeyRepository = idempotencyKeyRepository;
		this.objectMapper = objectMapper;
	}

	public TransferResponse execute(String idempotencyKey, TransferRequest request, Supplier<TransferResponse> action) {
		String normalizedKey = normalizeKey(idempotencyKey);
		String requestHash = hashRequest(request);
		OffsetDateTime now = OffsetDateTime.now();

		IdempotencyKeyEntity existingRecord = idempotencyKeyRepository.findByIdempotencyKey(normalizedKey).orElse(null);
		if (existingRecord != null) {
			return resolveExisting(existingRecord, requestHash);
		}

		IdempotencyKeyEntity newRecord = new IdempotencyKeyEntity(
				normalizedKey,
				requestHash,
				null,
				PROCESSING,
				now,
				now
		);

		try {
			idempotencyKeyRepository.saveAndFlush(newRecord);
		} catch (DataIntegrityViolationException exception) {
			IdempotencyKeyEntity persistedRecord = idempotencyKeyRepository.findByIdempotencyKey(normalizedKey)
					.orElseThrow(() -> exception);
			return resolveExisting(persistedRecord, requestHash);
		}

		TransferResponse response = action.get();
		newRecord.complete(writeResponse(response), response.status());
		idempotencyKeyRepository.save(newRecord);
		return response;
	}

	private TransferResponse resolveExisting(IdempotencyKeyEntity existingRecord, String requestHash) {
		if (!existingRecord.getRequestHash().equals(requestHash)) {
			throw new BusinessException(
					ErrorCode.IDEMPOTENCY_KEY_CONFLICT,
					"Idempotency key was already used with a different request body"
			);
		}

		if (existingRecord.getResponseBody() == null) {
			throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Idempotent request is still being processed");
		}

		return readResponse(existingRecord.getResponseBody());
	}

	private String normalizeKey(String idempotencyKey) {
		if (idempotencyKey == null || idempotencyKey.isBlank()) {
			throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
		}
		return idempotencyKey.trim();
	}

	private String hashRequest(TransferRequest request) {
		try {
			byte[] bytes = canonicalize(request).getBytes(StandardCharsets.UTF_8);
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(bytes));
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Unable to serialize transfer request", exception);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}

	private String canonicalize(TransferRequest request) throws JsonProcessingException {
		return objectMapper.writeValueAsString(new CanonicalTransferRequest(
				request.fromAccountNo(),
				request.toAccountNo(),
				request.amount() != null ? request.amount().toPlainString() : null,
				request.currency()
		));
	}

	private record CanonicalTransferRequest(
			String fromAccountNo,
			String toAccountNo,
			String amount,
			String currency
	) {
	}

	private String writeResponse(TransferResponse response) {
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Unable to serialize transfer response", exception);
		}
	}

	private TransferResponse readResponse(String responseBody) {
		try {
			return objectMapper.readValue(responseBody.getBytes(StandardCharsets.UTF_8), TransferResponse.class);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to deserialize transfer response", exception);
		}
	}
}
