package com.minh.bankingcore.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.transaction.TransferRequest;
import com.minh.bankingcore.transaction.TransferResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

	@Mock
	private IdempotencyKeyRepository idempotencyKeyRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private IdempotencyService idempotencyService;

	@BeforeEach
	void setUp() {
		idempotencyService = new IdempotencyService(idempotencyKeyRepository, objectMapper);
	}

	@Test
	void executeShouldPersistAndReturnNewResponse() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		TransferResponse response = new TransferResponse("TXN001", "SUCCESS", "100001", "100002", new BigDecimal("100000.00"), "VND");

		when(idempotencyKeyRepository.findByIdempotencyKey("idem-001")).thenReturn(Optional.empty());
		when(idempotencyKeyRepository.saveAndFlush(any(IdempotencyKeyEntity.class)))
				.thenAnswer(invocation -> invocation.getArgument(0, IdempotencyKeyEntity.class));
		when(idempotencyKeyRepository.save(any(IdempotencyKeyEntity.class)))
				.thenAnswer(invocation -> invocation.getArgument(0, IdempotencyKeyEntity.class));

		TransferResponse actual = idempotencyService.execute("idem-001", request, () -> response);

		assertThat(actual).isEqualTo(response);

		ArgumentCaptor<IdempotencyKeyEntity> recordCaptor = ArgumentCaptor.forClass(IdempotencyKeyEntity.class);
		verify(idempotencyKeyRepository).save(recordCaptor.capture());
		assertThat(recordCaptor.getValue().getStatus()).isEqualTo("SUCCESS");
		assertThat(recordCaptor.getValue().getResponseBody()).contains("TXN001");
	}

	@Test
	void executeShouldReplayStoredResponseForSameKeyAndRequest() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		IdempotencyKeyEntity existing = new IdempotencyKeyEntity(
				"idem-001",
				requestHash(request),
				"{\"referenceNo\":\"TXN001\",\"status\":\"SUCCESS\",\"fromAccountNo\":\"100001\",\"toAccountNo\":\"100002\",\"amount\":100000.00,\"currency\":\"VND\"}",
				"SUCCESS",
				OffsetDateTime.now(),
				OffsetDateTime.now()
		);

		when(idempotencyKeyRepository.findByIdempotencyKey("idem-001")).thenReturn(Optional.of(existing));

		TransferResponse actual = idempotencyService.execute("idem-001", request, () -> {
			throw new AssertionError("action should not be executed");
		});

		assertThat(actual.referenceNo()).isEqualTo("TXN001");
		verify(idempotencyKeyRepository, never()).saveAndFlush(any(IdempotencyKeyEntity.class));
	}

	@Test
	void executeShouldRejectSameKeyWithDifferentRequestBody() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		IdempotencyKeyEntity existing = new IdempotencyKeyEntity(
				"idem-001",
				"different-hash",
				"{\"referenceNo\":\"TXN001\"}",
				"SUCCESS",
				OffsetDateTime.now(),
				OffsetDateTime.now()
		);

		when(idempotencyKeyRepository.findByIdempotencyKey("idem-001")).thenReturn(Optional.of(existing));

		assertThatThrownBy(() -> idempotencyService.execute("idem-001", request, () -> null))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.IDEMPOTENCY_KEY_CONFLICT));
	}

	@Test
	void executeShouldReplayStoredResponseAfterConcurrentInsertConflict() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		IdempotencyKeyEntity existing = new IdempotencyKeyEntity(
				"idem-001",
				requestHash(request),
				"{\"referenceNo\":\"TXN001\",\"status\":\"SUCCESS\",\"fromAccountNo\":\"100001\",\"toAccountNo\":\"100002\",\"amount\":100000.00,\"currency\":\"VND\"}",
				"SUCCESS",
				OffsetDateTime.now(),
				OffsetDateTime.now()
		);

		when(idempotencyKeyRepository.findByIdempotencyKey("idem-001"))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(existing));
		when(idempotencyKeyRepository.saveAndFlush(any(IdempotencyKeyEntity.class)))
				.thenThrow(new DataIntegrityViolationException("duplicate key"));

		TransferResponse actual = idempotencyService.execute("idem-001", request, () -> {
			throw new AssertionError("action should not be executed");
		});

		assertThat(actual.referenceNo()).isEqualTo("TXN001");
	}

	@Test
	void executeShouldRejectMissingIdempotencyKey() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");

		assertThatThrownBy(() -> idempotencyService.execute("   ", request, () -> null))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.IDEMPOTENCY_KEY_REQUIRED));

		verify(idempotencyKeyRepository, never()).findByIdempotencyKey(any());
	}

	private String requestHash(TransferRequest request) {
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(new CanonicalTransferRequest(
					request.fromAccountNo(),
					request.toAccountNo(),
					request.amount() != null ? request.amount().toPlainString() : null,
					request.currency()
			));
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
		} catch (JsonProcessingException | NoSuchAlgorithmException exception) {
			throw new IllegalStateException(exception);
		}
	}

	private record CanonicalTransferRequest(
			String fromAccountNo,
			String toAccountNo,
			String amount,
			String currency
	) {
	}
}
