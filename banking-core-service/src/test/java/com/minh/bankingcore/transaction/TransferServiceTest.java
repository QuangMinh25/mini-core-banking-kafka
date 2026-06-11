package com.minh.bankingcore.transaction;

import com.minh.bankingcore.account.AccountEntity;
import com.minh.bankingcore.account.AccountRepository;
import com.minh.bankingcore.account.AccountStatus;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.kafka.TransactionCompletedEvent;
import com.minh.bankingcore.kafka.TransactionEventProducer;
import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private LedgerEntryRepository ledgerEntryRepository;

	@Mock
	private TransactionReferenceGenerator transactionReferenceGenerator;

	@Mock
	private TransactionEventProducer transactionEventProducer;

	@InjectMocks
	private TransferService transferService;

	@Test
	void transferShouldMoveBalancePersistRecordsAndPublishEvent() {
		AccountEntity sourceAccount = new AccountEntity("100001", "Nguyen Van A", new BigDecimal("1000000.00"), "VND", AccountStatus.ACTIVE);
		AccountEntity destinationAccount = new AccountEntity("100002", "Tran Thi B", new BigDecimal("500000.00"), "VND", AccountStatus.ACTIVE);
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");
		OffsetDateTime occurredAt = OffsetDateTime.parse("2026-06-11T22:30:01+07:00");

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

		TransferResponse response = transferService.transfer(request);

		assertThat(response.referenceNo()).isEqualTo("TXN202606112230001");
		assertThat(response.status()).isEqualTo("SUCCESS");
		assertThat(sourceAccount.getBalance()).isEqualByComparingTo("900000.00");
		assertThat(destinationAccount.getBalance()).isEqualByComparingTo("600000.00");
		verify(accountRepository).saveAll(List.of(sourceAccount, destinationAccount));

		ArgumentCaptor<List<LedgerEntryEntity>> ledgerCaptor = ArgumentCaptor.forClass(List.class);
		verify(ledgerEntryRepository).saveAll(ledgerCaptor.capture());
		assertThat(ledgerCaptor.getValue()).hasSize(2);

		ArgumentCaptor<TransactionCompletedEvent> eventCaptor = ArgumentCaptor.forClass(TransactionCompletedEvent.class);
		verify(transactionEventProducer).publish(eventCaptor.capture());
		assertThat(eventCaptor.getValue().referenceNo()).isEqualTo("TXN202606112230001");
	}

	@Test
	void transferShouldRejectSameSourceAndDestinationAccount() {
		TransferRequest request = new TransferRequest("100001", "100001", new BigDecimal("100000.00"), "VND");

		assertThatThrownBy(() -> transferService.transfer(request))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.SAME_ACCOUNT_TRANSFER));

		verify(accountRepository, never()).findAllByAccountNoInForUpdate(anyList());
	}

	@Test
	void transferShouldRejectInsufficientBalance() {
		AccountEntity sourceAccount = new AccountEntity("100001", "Nguyen Van A", new BigDecimal("50000.00"), "VND", AccountStatus.ACTIVE);
		AccountEntity destinationAccount = new AccountEntity("100002", "Tran Thi B", new BigDecimal("500000.00"), "VND", AccountStatus.ACTIVE);
		TransferRequest request = new TransferRequest("100001", "100002", new BigDecimal("100000.00"), "VND");

		when(accountRepository.findAllByAccountNoInForUpdate(anyList())).thenReturn(List.of(sourceAccount, destinationAccount));

		assertThatThrownBy(() -> transferService.transfer(request))
				.isInstanceOf(BusinessException.class)
				.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
						.isEqualTo(ErrorCode.INSUFFICIENT_BALANCE));

		verify(transactionRepository, never()).save(any(TransactionEntity.class));
		verify(transactionEventProducer, never()).publish(any(TransactionCompletedEvent.class));
	}
}
