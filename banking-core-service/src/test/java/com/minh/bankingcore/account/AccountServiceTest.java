package com.minh.bankingcore.account;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

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
    }

    @Test
    void createAccountRejectsDuplicateAccountNo() {
        when(accountRepository.existsByAccountNo("100001")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(new CreateAccountRequest("100001", "Nguyen Van A", BigDecimal.ZERO, "VND"))
        );

        assertEquals(ErrorCode.DUPLICATE_ACCOUNT_NO, exception.getErrorCode());
    }

    @Test
    void createAccountRejectsNegativeBalance() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(new CreateAccountRequest("100001", "Nguyen Van A", new BigDecimal("-1.00"), "VND"))
        );

        assertEquals(ErrorCode.INVALID_AMOUNT, exception.getErrorCode());
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
}
