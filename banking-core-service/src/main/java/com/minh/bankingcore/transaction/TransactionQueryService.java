package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.BusinessException;
import com.minh.bankingcore.common.ErrorCode;
import com.minh.bankingcore.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Locale;

@Service
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public TransactionQueryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(String referenceNo) {
        return transactionRepository.findByReferenceNo(referenceNo)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> listTransactions(
            int page,
            int size,
            String status,
            String type,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validatePageRequest(page, size, fromDate, toDate);

        TransactionStatus transactionStatus = parseStatus(status);
        TransactionType transactionType = parseType(type);
        OffsetDateTime fromDateTime = atStartOfDay(fromDate);
        OffsetDateTime toDateTime = atEndOfDay(toDate);

        Specification<TransactionEntity> specification = Specification.where(hasStatus(transactionStatus))
                .and(hasType(transactionType))
                .and(createdAtOnOrAfter(fromDateTime))
                .and(createdAtOnOrBefore(toDateTime));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TransactionResponse> responsePage = transactionRepository.findAll(specification, pageable)
                .map(TransactionResponse::from);
        return PageResponse.from(responsePage);
    }

    private void validatePageRequest(int page, int size, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "fromDate must be on or before toDate");
        }
        if (page < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "size must be greater than 0");
        }
    }

    private TransactionStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return TransactionStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Unsupported transaction status: " + status);
        }
    }

    private TransactionType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return TransactionType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Unsupported transaction type: " + type);
        }
    }

    private OffsetDateTime atStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private OffsetDateTime atEndOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().minusNanos(1);
    }

    private Specification<TransactionEntity> hasStatus(TransactionStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<TransactionEntity> hasType(TransactionType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    private Specification<TransactionEntity> createdAtOnOrAfter(OffsetDateTime fromDateTime) {
        return (root, query, criteriaBuilder) ->
                fromDateTime == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDateTime);
    }

    private Specification<TransactionEntity> createdAtOnOrBefore(OffsetDateTime toDateTime) {
        return (root, query, criteriaBuilder) ->
                toDateTime == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDateTime);
    }
}
