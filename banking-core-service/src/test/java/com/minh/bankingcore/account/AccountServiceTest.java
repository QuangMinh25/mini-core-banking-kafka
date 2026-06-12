package com.minh.bankingcore.account;

import com.minh.bankingcore.audit.AuditAction;
import com.minh.bankingcore.audit.AuditService;
import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.PageResponse;
import com.minh.bankingcore.ledger.LedgerEntryEntity;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import com.minh.bankingcore.ledger.LedgerEntryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountCreatesActiveAccount() {
        CreateAccountRequest request = new CreateAccountRequest("100001", "Nguyen Van A", new BigDecimal("1000000.00"), "VND");
        when(accountRepository.existsByAccountNo("100001")).thenReturn(false);
        when(accountRepository.save(any(AccountEntity.class))).thenAnswer(invocation -> {
            AccountEntity entity = invocation.getArgument(0);
            entity.setCreatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
            entity.setUpdatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
            return entity;
        });

        AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(AccountStatus.ACTIVE, captor.getValue().getStatus());
        assertEquals(new BigDecimal("1000000.00"), response.balance());
        assertEquals("100001", response.accountNo());
        verify(auditService).logSuccess(eq(AuditAction.CREATE_ACCOUNT), eq("ACCOUNT"), eq("100001"), any(), any());
    }

    @Test
    void createAccountRejectsDuplicateAccountNo() {
        when(accountRepository.existsByAccountNo("100001")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(new CreateAccountRequest("100001", "Nguyen Van A", BigDecimal.ZERO, "VND"))
        );

        assertEquals(ErrorCode.DUPLICATE_ACCOUNT_NO, exception.getErrorCode());
        verify(auditService).logFailure(eq(AuditAction.CREATE_ACCOUNT), eq("ACCOUNT"), eq("100001"), any(), isNull(), any());
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }

    @Test
    void createAccountRejectsNegativeBalance() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(new CreateAccountRequest("100001", "Nguyen Van A", new BigDecimal("-1.00"), "VND"))
        );

        assertEquals(ErrorCode.INVALID_AMOUNT, exception.getErrorCode());
        verify(auditService).logFailure(eq(AuditAction.CREATE_ACCOUNT), eq("ACCOUNT"), eq("100001"), any(), isNull(), any());
    }

    @Test
    void getAccountReturnsAccountDetails() {
        AccountEntity entity = new AccountEntity();
        entity.setAccountNo("100001");
        entity.setCustomerName("Nguyen Van A");
        entity.setBalance(new BigDecimal("1000000.00"));
        entity.setCurrency("VND");
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setCreatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
        entity.setUpdatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
        when(accountRepository.findByAccountNo("100001")).thenReturn(Optional.of(entity));

        AccountResponse response = accountService.getAccount("100001");

        assertEquals("100001", response.accountNo());
        assertEquals(AccountStatus.ACTIVE, response.status());
    }

    @Test
    void getAccountThrowsWhenNotFound() {
        when(accountRepository.findByAccountNo("100001")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.getAccount("100001"));

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void listAccountsReturnsPagedAccountsSortedByCreatedAtDesc() {
        AccountEntity entity = new AccountEntity();
        entity.setAccountNo("100001");
        entity.setCustomerName("Nguyen Van A");
        entity.setBalance(new BigDecimal("1000000.00"));
        entity.setCurrency("VND");
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setCreatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
        entity.setUpdatedAt(OffsetDateTime.parse("2026-06-11T23:00:00+07:00"));
        Page<AccountEntity> accountPage = new PageImpl<>(List.of(entity));
        when(accountRepository.findAll(any(Pageable.class))).thenReturn(accountPage);

        PageResponse<AccountResponse> response = accountService.listAccounts(0, 20);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(accountRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toString()).isEqualTo("createdAt: DESC");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).accountNo()).isEqualTo("100001");
    }

    @Test
    void getStatementReturnsPagedEntriesSortedByCreatedAtDesc() {
        LedgerEntryEntity entry = new LedgerEntryEntity(
                "TXN202606112230001",
                "100001",
                LedgerEntryType.DEBIT,
                new BigDecimal("100000.00"),
                new BigDecimal("900000.00"),
                "Transfer to 100002",
                OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
        );
        Page<LedgerEntryEntity> ledgerPage = new PageImpl<>(List.of(entry));

        when(accountRepository.existsByAccountNo("100001")).thenReturn(true);
        when(ledgerEntryRepository.findByAccountNoAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                eq("100001"),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class),
                any(Pageable.class)
        )).thenReturn(ledgerPage);

        AccountStatementResponse response = accountService.getStatement(
                "100001",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-12"),
                0,
                20
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(ledgerEntryRepository).findByAccountNoAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                eq("100001"),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class),
                pageableCaptor.capture()
        );

        assertThat(pageableCaptor.getValue().getSort().toString()).isEqualTo("createdAt: DESC");
        assertThat(response.entries()).hasSize(1);
        assertThat(response.entries().get(0).transactionReferenceNo()).isEqualTo("TXN202606112230001");
    }

    @Test
    void getStatementThrowsWhenAccountNotFound() {
        when(accountRepository.existsByAccountNo("999999")).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.getStatement("999999", null, null, 0, 20)
        );

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getStatementRejectsInvalidDateRange() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.getStatement("100001", LocalDate.parse("2026-06-12"), LocalDate.parse("2026-06-01"), 0, 20)
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals("fromDate must be on or before toDate", exception.getMessage());
    }
}
