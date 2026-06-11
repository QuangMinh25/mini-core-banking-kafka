package com.minh.bankingcore.account;

import com.minh.bankingcore.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }

    @GetMapping("/{accountNo}")
    public ApiResponse<AccountResponse> getAccount(@PathVariable String accountNo) {
        return ApiResponse.success(accountService.getAccount(accountNo));
    }
}
