package com.minh.bankingcore.outbox;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.GlobalExceptionHandler;
import com.minh.bankingcore.common.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OutboxEventControllerTest {

    private MockMvc mockMvc;
    private OutboxEventQueryService outboxEventQueryService;

    @BeforeEach
    void setUp() {
        outboxEventQueryService = Mockito.mock(OutboxEventQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OutboxEventController(outboxEventQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listOutboxEventsReturnsWrappedSuccessResponse() throws Exception {
        PageResponse<OutboxEventResponse> response = new PageResponse<>(
                List.of(new OutboxEventResponse(
                        "evt-001",
                        "transaction.completed",
                        "TransactionCompleted",
                        OutboxEventStatus.NEW,
                        0,
                        null,
                        OffsetDateTime.parse("2026-06-11T22:30:01+07:00"),
                        null
                )),
                0,
                20,
                1,
                1
        );
        Mockito.when(outboxEventQueryService.listOutboxEvents(0, 20, "NEW")).thenReturn(response);

        mockMvc.perform(get("/api/v1/outbox-events").param("status", "NEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].eventId").value("evt-001"));
    }

    @Test
    void getOutboxEventReturnsPayloadDetails() throws Exception {
        Mockito.when(outboxEventQueryService.getOutboxEvent("evt-001"))
                .thenReturn(new OutboxEventDetailResponse(
                        "evt-001",
                        "transaction.completed",
                        "TransactionCompleted",
                        "TXN001",
                        "{\"referenceNo\":\"TXN001\"}",
                        OutboxEventStatus.NEW,
                        0,
                        null,
                        OffsetDateTime.parse("2026-06-11T22:30:01+07:00"),
                        null
                ));

        mockMvc.perform(get("/api/v1/outbox-events/evt-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.payload").value("{\"referenceNo\":\"TXN001\"}"));
    }

    @Test
    void getOutboxEventReturnsNotFoundError() throws Exception {
        Mockito.when(outboxEventQueryService.getOutboxEvent("evt-404"))
                .thenThrow(new BusinessException(ErrorCode.OUTBOX_EVENT_NOT_FOUND));

        mockMvc.perform(get("/api/v1/outbox-events/evt-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("OUTBOX_EVENT_NOT_FOUND"));
    }
}
