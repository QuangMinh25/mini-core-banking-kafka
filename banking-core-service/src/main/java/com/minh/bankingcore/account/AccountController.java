package com.minh.bankingcore.account;

import com.minh.bankingcore.common.ApiResponse;
import com.minh.bankingcore.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account creation, listing, and statement APIs.")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new account",
            description = "Creates a banking account with the provided account number, customer name, opening balance, and currency."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid account request",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Create account request",
                                    value = """
                                            {
                                              "accountNo": "100001",
                                              "customerName": "Nguyen Van A",
                                              "balance": 1000000,
                                              "currency": "VND"
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }

    @GetMapping
    @Operation(summary = "List accounts", description = "Returns a paginated list of accounts.")
    public ApiResponse<PageResponse<AccountResponse>> listAccounts(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size.", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(accountService.listAccounts(page, size));
    }

    @GetMapping("/{accountNo}")
    @Operation(summary = "Get account by account number")
    public ApiResponse<AccountResponse> getAccount(@PathVariable String accountNo) {
        return ApiResponse.success(accountService.getAccount(accountNo));
    }

    @GetMapping("/{accountNo}/statement")
    @Operation(summary = "Get account statement", description = "Returns a paginated account statement with optional date filtering.")
    public ApiResponse<AccountStatementResponse> getStatement(
            @Parameter(description = "Account number.", example = "100001")
            @PathVariable String accountNo,
            @Parameter(description = "Filter statement entries from this date.", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter statement entries until this date.", example = "2026-06-13")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size.", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(accountService.getStatement(accountNo, fromDate, toDate, page, size));
    }
}
