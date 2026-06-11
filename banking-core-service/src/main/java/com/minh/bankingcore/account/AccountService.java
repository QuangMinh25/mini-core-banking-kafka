package com.minh.bankingcore.account;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
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

        try {
            return AccountResponse.from(accountRepository.save(accountEntity));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.DUPLICATE_ACCOUNT_NO);
        }
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .map(AccountResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void validateBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }
}
