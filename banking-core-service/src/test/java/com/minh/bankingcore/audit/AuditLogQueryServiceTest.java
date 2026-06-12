package com.minh.bankingcore.audit;

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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogQueryServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogQueryService auditLogQueryService;

    @Test
    void listAuditLogsReturnsSortedPage() {
        Page<AuditLogEntity> page = new PageImpl<>(List.of(new AuditLogEntity(
                "TRANSFER_MONEY",
                "TRANSACTION",
                "TXN001",
                "{\"fromAccountNo\":\"100001\"}",
                "{\"referenceNo\":\"TXN001\"}",
                "SUCCESS",
                null
        )));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<AuditLogResponse> response = auditLogQueryService.listAuditLogs(
                0,
                20,
                "TRANSFER_MONEY",
                "SUCCESS",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-12")
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(auditLogRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toString()).isEqualTo("createdAt: DESC");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).action()).isEqualTo("TRANSFER_MONEY");
    }

    @Test
    void listAuditLogsRejectsInvalidDateRange() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> auditLogQueryService.listAuditLogs(
                        0,
                        20,
                        null,
                        null,
                        LocalDate.parse("2026-06-12"),
                        LocalDate.parse("2026-06-01")
                )
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals("fromDate must be on or before toDate", exception.getMessage());
    }
}
