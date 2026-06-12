package com.minh.bankingcore.transaction;

import com.minh.bankingcore.account.AccountEntity;
import com.minh.bankingcore.account.AccountRepository;
import com.minh.bankingcore.account.AccountStatus;
import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.idempotency.IdempotencyService;
import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import com.minh.bankingcore.outbox.OutboxEventEntity;
import com.minh.bankingcore.outbox.OutboxEventFactory;
import com.minh.bankingcore.outbox.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

	private static final String IDEMPOTENCY_KEY = "idem-001";

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private LedgerEntryRepository ledgerEntryRepository;

	@Mock
	private TransactionReferenceGenerator transactionReferenceGenerator;

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private OutboxEventFactory outboxEventFactory;

	@Mock
	private IdempotencyService idempotencyService;

	@Mock
	private AuditService auditService;

	@InjectMocks
	private TransferService transferService;

	@Test
	void transferShouldMoveBalancePersistRecordsAndCreateOutboxEvent() {
		AccountEntity sourceAccount = new AccountEntity("100001", "Nguyen Van A", new BigDecimal("1000000.00"), "VND", AccountStatus.ACTIVE);
		AccountEntity destinationAccount = new AccountEntity("100002", "Tran Thi B", new BigDecimal("500000.00"), "VND", AccountStatus.ACTIVE);
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		OffsetDateTime occurredAt = OffsetDateTime.parse("2026-06-11T22:30:01+07:00");

		mockIdempotencyExecution();
		when(accountRepository.findAllByAccountNoInForUpdate(anyList())).thenReturn(List.of(sourceAccount, destinationAccount));
		when(transactionReferenceGenerator.generate()).thenReturn("TXN202606112230001");
		when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> {
			TransactionEntity transaction = invocation.getArgument(0, TransactionEntity.class);
			return new TransactionEntity(
					transaction.getReferenceNo(),
					TransactionType.TRANSFER,
					TransactionStatus.SUCCESS,
					transaction.getFromAccountNo(),
					transaction.getToAccountNo(),
					transaction.getAmount(),
					transaction.getCurrency(),
					occurredAt
			);
		});

		OutboxEventEntity outboxEvent = org.mockito.Mockito.mock(OutboxEventEntity.class);
		when(outboxEventFactory.createTransactionCompletedEvent(any())).thenReturn(outboxEvent);

		TransferResponse response = transferService.transfer(IDEMPOTENCY_KEY, request);

		assertThat(response.referenceNo()).isEqualTo("TXN202606112230001");
		assertThat(response.status()).isEqualTo("SUCCESS");
		assertThat(sourceAccount.getBalance()).isEqualByComparingTo("900000.00");
		assertThat(destinationAccount.getBalance()).isEqualByComparingTo("600000.00");
		verify(accountRepository).saveAll(List.of(sourceAccount, destinationAccount));

		ArgumentCaptor<List<LedgerEntryEntity>> ledgerCaptor = ArgumentCaptor.forClass(List.class);
		verify(ledgerEntryRepository).saveAll(ledgerCaptor.capture());
		assertThat(ledgerCaptor.getValue()).hasSize(2);

		verify(outboxEventFactory).createTransactionCompletedEvent(any());
		verify(outboxEventRepository).save(outboxEvent);
		verify(auditService).logSuccess(eq(AuditAction.CREATE_OUTBOX_EVENT), eq("OUTBOX_EVENT"), eq("TXN202606112230001"), any(), any());
		verify(auditService).logSuccess(eq(AuditAction.TRANSFER_MONEY), eq("TRANSACTION"), eq("TXN202606112230001"), any(), any());
	}

	@Test
	void transferShouldLockAccountsInStableOrder() {
		AccountEntity sourceAccount = new AccountEntity("100002", "Tran Thi B", new BigDecimal("1000000.00"), "VND", AccountStatus.ACTIVE);
		AccountEntity destinationAccount = new AccountEntity("100001", "Nguyen Van A", new BigDecimal("500000.00"), "VND", AccountStatus.ACTIVE);
		TransferRequest request = new TransferRequest("100002", "100001", new BigDecimal("100000.00"), "VND");
		OffsetDateTime occurredAt = OffsetDateTime.parse("2026-06-11T22:30:01+07:00");

		mockIdempotencyExecution();
		when(accountRepository.findAllByAccountNoInForUpdate(anyList())).thenReturn(List.of(destinationAccount, sourceAccount));
		when(transactionReferenceGenerator.generate()).thenReturn("TXN202606112230002");
		when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> {
			TransactionEntity transaction = invocation.getArgument(0, TransactionEntity.class);
			return new TransactionEntity(
					transaction.getReferenceNo(),
					TransactionType.TRANSFER,
					TransactionStatus.SUCCESS,
					transaction.getFromAccountNo(),
					transaction.getToAccountNo(),
					transaction.getAmount(),
					transaction.getCurrency(),
					occurredAt
			);
		});
		when(outboxEventFactory.createTransactionCompletedEvent(any())).thenReturn(org.mockito.Mockito.mock(OutboxEventEntity.class));

		transferService.transfer(IDEMPOTENCY_KEY, request);

		ArgumentCaptor<List<String>> accountNumbersCaptor = ArgumentCaptor.forClass(List.class);
		verify(accountRepository).findAllByAccountNoInForUpdate(accountNumbersCaptor.capture());
		assertThat(accountNumbersCaptor.getValue()).containsExactly("100001", "100002");
		assertThat(sourceAccount.getBalance()).isEqualByComparingTo("900000.00");
		assertThat(destinationAccount.getBalance()).isEqualByComparingTo("600000.00");
	}

	@Test
	void transferShouldRejectSameSourceAndDestinationAccount() {
		TransferRequest request = new TransferRequest("100001", "100001", new BigDecimal("100000.00"), "VND");
		mockIdempotencyExecution();

		assertThatThrownBy(() -> transferService.transfer(IDEMPOTENCY_KEY, request))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.SAME_ACCOUNT_TRANSFER));

		verify(accountRepository, never()).findAllByAccountNoInForUpdate(anyList());
		verify(auditService).logFailure(eq(AuditAction.TRANSFER_MONEY), eq("TRANSACTION"), eq("100001->100001"), any(), isNull(), any());
	}

	@Test
	void transferShouldRejectInsufficientBalance() {
		AccountEntity sourceAccount = new AccountEntity("100001", "Nguyen Van A", new BigDecimal("50000.00"), "VND", AccountStatus.ACTIVE);
		AccountEntity destinationAccount = new AccountEntity("100002", "Tran Thi B", new BigDecimal("500000.00"), "VND", AccountStatus.ACTIVE);
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");

		mockIdempotencyExecution();
		when(accountRepository.findAllByAccountNoInForUpdate(anyList())).thenReturn(List.of(sourceAccount, destinationAccount));

		assertThatThrownBy(() -> transferService.transfer(IDEMPOTENCY_KEY, request))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.INSUFFICIENT_BALANCE));

		verify(transactionRepository, never()).save(any(TransactionEntity.class));
		verify(outboxEventRepository, never()).save(any(OutboxEventEntity.class));
		verify(auditService).logFailure(eq(AuditAction.TRANSFER_MONEY), eq("TRANSACTION"), eq("100001->100002"), any(), isNull(), any());
	}

	@Test
	void transferShouldDelegateToIdempotencyService() {
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		TransferResponse response = new TransferResponse("TXN001", "SUCCESS", "100001", "100002", new BigDecimal("100000.00"), "VND");

		when(idempotencyService.execute(org.mockito.ArgumentMatchers.eq(IDEMPOTENCY_KEY), org.mockito.ArgumentMatchers.eq(request), any()))
				.thenReturn(response);

		TransferResponse actual = transferService.transfer(IDEMPOTENCY_KEY, request);

		assertThat(actual).isEqualTo(response);
		verify(idempotencyService).execute(org.mockito.ArgumentMatchers.eq(IDEMPOTENCY_KEY), org.mockito.ArgumentMatchers.eq(request), any());
	}

	private void mockIdempotencyExecution() {
		when(idempotencyService.execute(any(), any(TransferRequest.class), any())).thenAnswer(invocation -> {
			Supplier<TransferResponse> action = invocation.getArgument(2);
			return action.get();
		});
	}
}
