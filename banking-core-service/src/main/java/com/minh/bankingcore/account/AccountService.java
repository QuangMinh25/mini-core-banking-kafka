package com.minh.bankingcore.account;

import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.PageResponse;
import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AuditService auditService;

    public AccountService(
            AccountRepository accountRepository,
            LedgerEntryRepository ledgerEntryRepository,
            AuditService auditService
    ) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.auditService = auditService;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Map<String, Object> auditRequest = createAccountRequestData(request);

        try {
            BigDecimal openingBalance = request.balance() == null ? BigDecimal.ZERO : request.balance();
            validateBalance(openingBalance);

            if (accountRepository.existsByAccountNo(request.accountNo())) {
                throw new BusinessException(ErrorCode.DUPLICATE_ACCOUNT_NO);
            }

            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setAccountNo(request.accountNo());
            accountEntity.setCustomerName(request.customerName());
            accountEntity.setBalance(openingBalance);
            accountEntity.setCurrency(request.currency());
            accountEntity.setStatus(AccountStatus.ACTIVE);

            AccountResponse response = AccountResponse.from(accountRepository.save(accountEntity));
            auditService.logSuccess(
                    AuditAction.CREATE_ACCOUNT,
                    "ACCOUNT",
                    response.accountNo(),
                    auditRequest,
                    Map.of(
                            "accountNo", response.accountNo(),
                            "currency", response.currency(),
                            "status", response.status().name(),
                            "balance", response.balance()
                    )
            );
            return response;
        } catch (DataIntegrityViolationException exception) {
            auditService.logFailure(AuditAction.CREATE_ACCOUNT, "ACCOUNT", request.accountNo(), auditRequest, null, exception);
            throw new BusinessException(ErrorCode.DUPLICATE_ACCOUNT_NO);
        } catch (RuntimeException exception) {
            auditService.logFailure(AuditAction.CREATE_ACCOUNT, "ACCOUNT", request.accountNo(), auditRequest, null, exception);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .map(AccountResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> listAccounts(int page, int size) {
        validatePageRequest(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AccountResponse> responsePage = accountRepository.findAll(pageable).map(AccountResponse::from);
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public AccountStatementResponse getStatement(String accountNo, LocalDate fromDate, LocalDate toDate, int page, int size) {
        validatePageRequest(page, size);
        validateDateRange(fromDate, toDate);
        ensureAccountExists(accountNo);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime toDateTime = toDate == null ? null : toDate.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime().minusNanos(1);

        Page<LedgerEntryEntity> ledgerEntries = findStatementEntries(accountNo, fromDateTime, toDateTime, pageable);
        Page<AccountStatementEntryResponse> responsePage = ledgerEntries.map(AccountStatementEntryResponse::from);
        return AccountStatementResponse.from(responsePage);
    }

    private void validateBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "fromDate must be on or before toDate");
        }
    }

    private void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "page must be greater than or equal to 0");
        }

        if (size <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "size must be greater than 0");
        }
    }

    private void ensureAccountExists(String accountNo) {
        if (!accountRepository.existsByAccountNo(accountNo)) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }

    private Map<String, Object> createAccountRequestData(CreateAccountRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("accountNo", request.accountNo());
        payload.put("currency", request.currency());
        payload.put("openingBalance", request.balance() == null ? BigDecimal.ZERO : request.balance());
        return payload;
    }

    private Page<LedgerEntryEntity> findStatementEntries(
            String accountNo,
            OffsetDateTime fromDateTime,
            OffsetDateTime toDateTime,
            Pageable pageable
    ) {
        if (fromDateTime != null && toDateTime != null) {
            return ledgerEntryRepository.findByAccountNoAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                    accountNo,
                    fromDateTime,
                    toDateTime,
                    pageable
            );
        }

        if (fromDateTime != null) {
            return ledgerEntryRepository.findByAccountNoAndCreatedAtGreaterThanEqual(accountNo, fromDateTime, pageable);
        }

        if (toDateTime != null) {
            return ledgerEntryRepository.findByAccountNoAndCreatedAtLessThanEqual(accountNo, toDateTime, pageable);
        }

        return ledgerEntryRepository.findByAccountNo(accountNo, pageable);
    }
}
