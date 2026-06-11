package com.minh.bankingcore.transaction;

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
        Mockito.when(transferService.transfer(Mockito.any(TransferRequest.class)))
                .thenReturn(new TransferResponse(
                        "TXN202606112230001",
                        "SUCCESS",
                        "100001",
                        "100002",
                        new BigDecimal("100000.00"),
                        "VND"
                ));

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.referenceNo").value("TXN202606112230001"))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }
}
