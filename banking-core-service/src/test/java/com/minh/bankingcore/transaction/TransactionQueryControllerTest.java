package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.GlobalExceptionHandler;
import com.minh.bankingcore.common.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionQueryControllerTest {

    private MockMvc mockMvc;
    private TransactionQueryService transactionQueryService;

    @BeforeEach
    void setUp() {
        transactionQueryService = Mockito.mock(TransactionQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionQueryController(transactionQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTransactionReturnsWrappedSuccessResponse() throws Exception {
        Mockito.when(transactionQueryService.getTransaction("TXN001"))
                .thenReturn(new TransactionResponse(
                        "TXN001",
                        "100001",
                        "100002",
                        new BigDecimal("100000.00"),
                        "VND",
                        TransactionType.TRANSFER,
                        TransactionStatus.SUCCESS,
                        OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
                ));

        mockMvc.perform(get("/api/v1/transactions/TXN001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.referenceNo").value("TXN001"))
                .andExpect(jsonPath("$.data.type").value("TRANSFER"));
    }

    @Test
    void getTransactionReturnsNotFoundError() throws Exception {
        Mockito.when(transactionQueryService.getTransaction("TXN404"))
                .thenThrow(new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        mockMvc.perform(get("/api/v1/transactions/TXN404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRANSACTION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Transaction not found"));
    }

    @Test
    void listTransactionsReturnsWrappedSuccessResponse() throws Exception {
        PageResponse<TransactionResponse> response = new PageResponse<>(
                List.of(new TransactionResponse(
                        "TXN001",
                        "100001",
                        "100002",
                        new BigDecimal("100000.00"),
                        "VND",
                        TransactionType.TRANSFER,
                        TransactionStatus.SUCCESS,
                        OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
                )),
                0,
                20,
                1,
                1
        );
        Mockito.when(transactionQueryService.listTransactions(
                0,
                20,
                "SUCCESS",
                "TRANSFER",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-12")
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("status", "SUCCESS")
                        .param("type", "TRANSFER")
                        .param("fromDate", "2026-06-01")
                        .param("toDate", "2026-06-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].referenceNo").value("TXN001"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
