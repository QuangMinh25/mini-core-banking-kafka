package com.minh.bankingcore.account;
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
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest {

    private MockMvc mockMvc;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createAccountReturnsWrappedSuccessResponse() throws Exception {
        AccountResponse response = new AccountResponse(
                "100001",
                "Nguyen Van A",
                new BigDecimal("1000000.00"),
                "VND",
                AccountStatus.ACTIVE,
                OffsetDateTime.parse("2026-06-11T23:00:00+07:00"),
                OffsetDateTime.parse("2026-06-11T23:00:00+07:00")
        );
        Mockito.when(accountService.createAccount(Mockito.any(CreateAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountNo": "100001",
                                  "customerName": "Nguyen Van A",
                                  "balance": 1000000.00,
                                  "currency": "VND"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountNo").value("100001"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void getAccountReturnsAccountNotFoundError() throws Exception {
        Mockito.when(accountService.getAccount("999999"))
                .thenThrow(new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        mockMvc.perform(get("/api/v1/accounts/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void createAccountReturnsValidationErrorForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request body is invalid"));
    }
}
