package com.minh.bankingcore.transaction;

import com.minh.bankingcore.account.AccountEntity;
import com.minh.bankingcore.account.AccountRepository;
import com.minh.bankingcore.account.AccountStatus;
import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.idempotency.IdempotencyService;
import com.minh.bankingcore.kafka.TransactionCompletedEvent;
import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import com.minh.bankingcore.ledger.LedgerEntryType;
import com.minh.bankingcore.outbox.OutboxEventFactory;
import com.minh.bankingcore.outbox.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
	private final OutboxEventRepository outboxEventRepository;
	private final OutboxEventFactory outboxEventFactory;
	private final IdempotencyService idempotencyService;
	private final AuditService auditService;

	public TransferService(
			AccountRepository accountRepository,
			TransactionRepository transactionRepository,
			LedgerEntryRepository ledgerEntryRepository,
			TransactionReferenceGenerator transactionReferenceGenerator,
			OutboxEventRepository outboxEventRepository,
			OutboxEventFactory outboxEventFactory,
			IdempotencyService idempotencyService,
			AuditService auditService
	) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
		this.ledgerEntryRepository = ledgerEntryRepository;
		this.transactionReferenceGenerator = transactionReferenceGenerator;
		this.outboxEventRepository = outboxEventRepository;
		this.outboxEventFactory = outboxEventFactory;
		this.idempotencyService = idempotencyService;
		this.auditService = auditService;
	}

	@Transactional
	public TransferResponse transfer(String idempotencyKey, TransferRequest request) {
		return idempotencyService.execute(idempotencyKey, request, () -> processTransfer(request));
	}

	TransferResponse processTransfer(TransferRequest request) {
		Map<String, Object> auditRequest = transferRequestData(request);
		try {
			validateRequest(request);

			List<AccountEntity> accounts = loadAccountsForTransfer(request);
			Map<String, AccountEntity> accountsByNumber = accounts.stream()
					.collect(Collectors.toMap(AccountEntity::getAccountNo, Function.identity()));

			AccountEntity sourceAccount = accountsByNumber.get(request.fromAccountNo());
			AccountEntity destinationAccount = accountsByNumber.get(request.toAccountNo());

			if (sourceAccount == null || destinationAccount == null) {
				throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
			}

			validateAccounts(sourceAccount, destinationAccount, request.currency(), request.amount());

			BigDecimal updatedSourceBalance = sourceAccount.getBalance().subtract(request.amount());
			if (updatedSourceBalance.compareTo(BigDecimal.ZERO) < 0) {
				throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
			}
			sourceAccount.setBalance(updatedSourceBalance);
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
					TransactionCompletedEvent.EVENT_TYPE,
					transaction.getReferenceNo(),
					transaction.getFromAccountNo(),
					transaction.getToAccountNo(),
					transaction.getAmount(),
					transaction.getCurrency(),
					transaction.getCreatedAt().toLocalDateTime()
			);
			Map<String, Object> outboxRequest = outboxRequestData(event);
			try {
				outboxEventRepository.save(outboxEventFactory.createTransactionCompletedEvent(event));
				auditService.logSuccess(
						AuditAction.CREATE_OUTBOX_EVENT,
						"OUTBOX_EVENT",
						event.referenceNo(),
						outboxRequest,
						Map.of("status", "NEW")
				);
			} catch (RuntimeException exception) {
				auditService.logFailure(
						AuditAction.CREATE_OUTBOX_EVENT,
						"OUTBOX_EVENT",
						event.referenceNo(),
						outboxRequest,
						null,
						exception
				);
				throw exception;
			}

			TransferResponse response = new TransferResponse(
					transaction.getReferenceNo(),
					transaction.getStatus().name(),
					transaction.getFromAccountNo(),
					transaction.getToAccountNo(),
					transaction.getAmount(),
					transaction.getCurrency()
			);
			auditService.logSuccess(
					AuditAction.TRANSFER_MONEY,
					"TRANSACTION",
					response.referenceNo(),
					auditRequest,
					transferResponseData(response)
			);
			return response;
		} catch (RuntimeException exception) {
			auditService.logFailure(
					AuditAction.TRANSFER_MONEY,
					"TRANSACTION",
					request.fromAccountNo() + "->" + request.toAccountNo(),
					auditRequest,
					null,
					exception
			);
			throw exception;
		}
	}

	private List<AccountEntity> loadAccountsForTransfer(TransferRequest request) {
		List<String> accountNumbers = List.of(request.fromAccountNo(), request.toAccountNo()).stream()
				.sorted(Comparator.naturalOrder())
				.toList();
		return accountRepository.findAllByAccountNoInForUpdate(accountNumbers);
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

	private Map<String, Object> transferRequestData(TransferRequest request) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("fromAccountNo", request.fromAccountNo());
		payload.put("toAccountNo", request.toAccountNo());
		payload.put("amount", request.amount());
		payload.put("currency", request.currency());
		return payload;
	}

	private Map<String, Object> transferResponseData(TransferResponse response) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("referenceNo", response.referenceNo());
		payload.put("status", response.status());
		payload.put("fromAccountNo", response.fromAccountNo());
		payload.put("toAccountNo", response.toAccountNo());
		payload.put("amount", response.amount());
		payload.put("currency", response.currency());
		return payload;
	}

	private Map<String, Object> outboxRequestData(TransactionCompletedEvent event) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("eventId", event.eventId());
		payload.put("eventType", event.eventType());
		payload.put("referenceNo", event.referenceNo());
		payload.put("fromAccountNo", event.fromAccountNo());
		payload.put("toAccountNo", event.toAccountNo());
		payload.put("amount", event.amount());
		payload.put("currency", event.currency());
		return payload;
	}
}
