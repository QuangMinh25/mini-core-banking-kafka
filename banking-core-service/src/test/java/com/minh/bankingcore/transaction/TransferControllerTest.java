package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferControllerTest {

    private MockMvc mockMvc;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = Mockito.mock(TransferService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TransferController(transferService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void transferReturnsWrappedSuccessResponse() throws Exception {
        Mockito.when(transferService.transfer(Mockito.eq("idem-001"), Mockito.any(TransferRequest.class)))
                .thenReturn(new TransferResponse(
                        "TXN202606112230001",
                        "SUCCESS",
                        "100001",
                        "100002",
                        new BigDecimal("100000.00"),
                        "VND"
                ));

        mockMvc.perform(post("/api/v1/transfers")
                        .header(TransferController.IDEMPOTENCY_KEY_HEADER, "idem-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountNo": "100001",
                                  "toAccountNo": "100002",
                                  "amount": 100000.00,
                                  "currency": "VND"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.referenceNo").value("TXN202606112230001"))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }

    @Test
    void transferRequiresIdempotencyKeyHeader() throws Exception {
        Mockito.when(transferService.transfer(Mockito.isNull(), Mockito.any(TransferRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountNo": "100001",
                                  "toAccountNo": "100002",
                                  "amount": 100000.00,
                                  "currency": "VND"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Idempotency-Key header is required"));
    }

    @Test
    void transferReturnsValidationErrorForMissingAccountNumber() throws Exception {
        mockMvc.perform(post("/api/v1/transfers")
                        .header(TransferController.IDEMPOTENCY_KEY_HEADER, "idem-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountNo": "",
                                  "toAccountNo": "100002",
                                  "amount": 100000.00,
                                  "currency": "VND"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
