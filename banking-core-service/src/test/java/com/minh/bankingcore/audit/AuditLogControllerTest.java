package com.minh.bankingcore.audit;

import com.minh.bankingcore.common.GlobalExceptionHandler;
import com.minh.bankingcore.common.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditLogControllerTest {

    private MockMvc mockMvc;
    private AuditLogQueryService auditLogQueryService;

    @BeforeEach
    void setUp() {
        auditLogQueryService = Mockito.mock(AuditLogQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuditLogController(auditLogQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listAuditLogsReturnsWrappedSuccessResponse() throws Exception {
        PageResponse<AuditLogResponse> response = new PageResponse<>(
                List.of(new AuditLogResponse(
                        "TRANSFER_MONEY",
                        "TRANSACTION",
                        "TXN001",
                        "{\"fromAccountNo\":\"100001\"}",
                        "{\"referenceNo\":\"TXN001\"}",
                        "SUCCESS",
                        null,
                        OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
                )),
                0,
                20,
                1,
                1
        );
        Mockito.when(auditLogQueryService.listAuditLogs(
                0,
                20,
                "TRANSFER_MONEY",
                "SUCCESS",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-12")
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/audit-logs")
                        .param("action", "TRANSFER_MONEY")
                        .param("status", "SUCCESS")
                        .param("fromDate", "2026-06-01")
                        .param("toDate", "2026-06-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].action").value("TRANSFER_MONEY"));
    }
}
