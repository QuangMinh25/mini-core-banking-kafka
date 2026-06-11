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
import com.minh.bankingcore.ledger.LedgerEntryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransferService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final LedgerEntryRepository ledgerEntryRepository;
	private final TransactionReferenceGenerator transactionReferenceGenerator;
	private final TransactionEventProducer transactionEventProducer;

	public TransferService(
			AccountRepository accountRepository,
			TransactionRepository transactionRepository,
			LedgerEntryRepository ledgerEntryRepository,
			TransactionReferenceGenerator transactionReferenceGenerator,
			TransactionEventProducer transactionEventProducer
	) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
		this.ledgerEntryRepository = ledgerEntryRepository;
		this.transactionReferenceGenerator = transactionReferenceGenerator;
		this.transactionEventProducer = transactionEventProducer;
	}

	@Transactional
	public TransferResponse transfer(TransferRequest request) {
		validateRequest(request);

		List<AccountEntity> accounts = accountRepository.findAllByAccountNoInForUpdate(
				List.of(request.fromAccountNo(), request.toAccountNo())
		);
		Map<String, AccountEntity> accountsByNumber = accounts.stream()
				.collect(Collectors.toMap(AccountEntity::getAccountNo, Function.identity()));

		AccountEntity sourceAccount = accountsByNumber.get(request.fromAccountNo());
		AccountEntity destinationAccount = accountsByNumber.get(request.toAccountNo());

		if (sourceAccount == null || destinationAccount == null) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
		}

		validateAccounts(sourceAccount, destinationAccount, request.currency(), request.amount());

		sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
		destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));
		accountRepository.saveAll(List.of(sourceAccount, destinationAccount));

		String referenceNo = transactionReferenceGenerator.generate();
		OffsetDateTime occurredAt = OffsetDateTime.now();

		TransactionEntity transaction = transactionRepository.save(new TransactionEntity(
				referenceNo,
				TransactionType.TRANSFER,
				TransactionStatus.SUCCESS,
				sourceAccount.getAccountNo(),
				destinationAccount.getAccountNo(),
				request.amount(),
				request.currency(),
				occurredAt
		));

		ledgerEntryRepository.saveAll(List.of(
				new LedgerEntryEntity(
						referenceNo,
						sourceAccount.getAccountNo(),
						LedgerEntryType.DEBIT,
						request.amount(),
						sourceAccount.getBalance(),
						"Transfer to " + destinationAccount.getAccountNo(),
						occurredAt
				),
				new LedgerEntryEntity(
						referenceNo,
						destinationAccount.getAccountNo(),
						LedgerEntryType.CREDIT,
						request.amount(),
						destinationAccount.getBalance(),
						"Transfer from " + sourceAccount.getAccountNo(),
						occurredAt
				)
		));

		TransactionCompletedEvent event = new TransactionCompletedEvent(
				UUID.randomUUID().toString(),
				"TRANSACTION_COMPLETED",
				transaction.getReferenceNo(),
				transaction.getFromAccountNo(),
				transaction.getToAccountNo(),
				transaction.getAmount(),
				transaction.getCurrency(),
				transaction.getCreatedAt().toLocalDateTime()
		);

		publishAfterCommit(event);

		return new TransferResponse(
				transaction.getReferenceNo(),
				transaction.getStatus().name(),
				transaction.getFromAccountNo(),
				transaction.getToAccountNo(),
				transaction.getAmount(),
				transaction.getCurrency()
		);
	}

	private void validateRequest(TransferRequest request) {
		if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException(ErrorCode.INVALID_AMOUNT, "Amount must be greater than zero");
		}

		if (request.fromAccountNo() != null && request.fromAccountNo().equals(request.toAccountNo())) {
			throw new BusinessException(
					ErrorCode.SAME_ACCOUNT_TRANSFER,
					"Source and destination accounts must be different"
			);
		}
	}

	private void validateAccounts(
			AccountEntity sourceAccount,
			AccountEntity destinationAccount,
			String requestCurrency,
			BigDecimal amount
	) {
		if (sourceAccount.getStatus() != AccountStatus.ACTIVE || destinationAccount.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE, "Both accounts must be active");
		}

		if (!sourceAccount.getCurrency().equals(requestCurrency)
				|| !destinationAccount.getCurrency().equals(requestCurrency)
				|| !sourceAccount.getCurrency().equals(destinationAccount.getCurrency())) {
			throw new BusinessException(ErrorCode.CURRENCY_MISMATCH, "Currency must match both accounts");
		}

		if (sourceAccount.getBalance().compareTo(amount) < 0) {
			throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
		}
	}

	private void publishAfterCommit(TransactionCompletedEvent event) {
		if (TransactionSynchronizationManager.isActualTransactionActive()
				&& TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					transactionEventProducer.publish(event);
				}
			});
			return;
		}

		transactionEventProducer.publish(event);
	}
}
