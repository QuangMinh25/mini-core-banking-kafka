package com.minh.bankingcore.transaction;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionQueryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionQueryService transactionQueryService;

    @Test
    void getTransactionReturnsMappedResponse() {
        TransactionEntity entity = new TransactionEntity(
                "TXN001",
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "100001",
                "100002",
                new BigDecimal("100000.00"),
                "VND",
                OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
        );
        when(transactionRepository.findByReferenceNo("TXN001")).thenReturn(Optional.of(entity));

        TransactionResponse response = transactionQueryService.getTransaction("TXN001");

        assertEquals("TXN001", response.referenceNo());
        assertEquals(TransactionType.TRANSFER, response.type());
    }

    @Test
    void getTransactionThrowsWhenNotFound() {
        when(transactionRepository.findByReferenceNo("TXN404")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionQueryService.getTransaction("TXN404")
        );

        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void listTransactionsAppliesDescSortAndReturnsPageResponse() {
        Page<TransactionEntity> page = new PageImpl<>(List.of(new TransactionEntity(
                "TXN001",
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "100001",
                "100002",
                new BigDecimal("100000.00"),
                "VND",
                OffsetDateTime.parse("2026-06-11T22:30:01+07:00")
        )));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<TransactionResponse> response = transactionQueryService.listTransactions(
                0,
                20,
                "SUCCESS",
                "TRANSFER",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-12")
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactionRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toString()).isEqualTo("createdAt: DESC");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).referenceNo()).isEqualTo("TXN001");
    }

    @Test
    void listTransactionsRejectsUnsupportedStatus() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionQueryService.listTransactions(0, 20, "UNKNOWN", null, null, null)
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals("Unsupported transaction status: UNKNOWN", exception.getMessage());
    }
}
