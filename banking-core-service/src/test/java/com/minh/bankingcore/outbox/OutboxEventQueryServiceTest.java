package com.minh.bankingcore.outbox;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventQueryServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxEventQueryService outboxEventQueryService;

    @Test
    void listOutboxEventsReturnsSortedPage() {
        Page<OutboxEventEntity> page = new PageImpl<>(List.of(new OutboxEventEntity(
                "evt-001",
                "TransactionCompleted",
                "TXN001",
                "transaction.completed",
                "{\"referenceNo\":\"TXN001\"}",
                OutboxEventStatus.NEW
        )));
        when(outboxEventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<OutboxEventResponse> response = outboxEventQueryService.listOutboxEvents(0, 20, "NEW");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(outboxEventRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toString()).isEqualTo("createdAt: DESC");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).eventId()).isEqualTo("evt-001");
    }

    @Test
    void getOutboxEventReturnsMappedDetail() {
        OutboxEventEntity entity = new OutboxEventEntity(
                "evt-001",
                "TransactionCompleted",
                "TXN001",
                "transaction.completed",
                "{\"referenceNo\":\"TXN001\"}",
                OutboxEventStatus.NEW
        );
        when(outboxEventRepository.findByEventId("evt-001")).thenReturn(Optional.of(entity));

        OutboxEventDetailResponse response = outboxEventQueryService.getOutboxEvent("evt-001");

        assertEquals("evt-001", response.eventId());
        assertEquals("{\"referenceNo\":\"TXN001\"}", response.payload());
    }

    @Test
    void listOutboxEventsRejectsUnsupportedStatus() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> outboxEventQueryService.listOutboxEvents(0, 20, "INVALID")
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals("Unsupported outbox status: INVALID", exception.getMessage());
    }
}
