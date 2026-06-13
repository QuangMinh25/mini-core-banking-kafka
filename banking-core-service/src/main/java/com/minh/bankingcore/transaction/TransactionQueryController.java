package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.ApiResponse;
import com.minh.bankingcore.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction query APIs.")
public class TransactionQueryController {

    private final TransactionQueryService transactionQueryService;

    public TransactionQueryController(TransactionQueryService transactionQueryService) {
        this.transactionQueryService = transactionQueryService;
    }

    @GetMapping("/{referenceNo}")
    @Operation(summary = "Get transaction by reference number")
    public ApiResponse<TransactionResponse> getTransaction(@PathVariable String referenceNo) {
        return ApiResponse.success(transactionQueryService.getTransaction(referenceNo));
    }

    @GetMapping
    @Operation(summary = "List transactions", description = "Returns paginated transactions with optional status, type, and date filters.")
    public ApiResponse<PageResponse<TransactionResponse>> listTransactions(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size.", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Transaction status filter.", example = "SUCCESS")
            @RequestParam(required = false) String status,
            @Parameter(description = "Transaction type filter.", example = "TRANSFER")
            @RequestParam(required = false) String type,
            @Parameter(description = "Filter transactions from this date.", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter transactions until this date.", example = "2026-06-13")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ApiResponse.success(transactionQueryService.listTransactions(page, size, status, type, fromDate, toDate));
    }
}
