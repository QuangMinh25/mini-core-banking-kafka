package com.minh.bankingcore.audit;

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

@Service
public class AuditLogQueryService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> listAuditLogs(
            int page,
            int size,
            String action,
            String status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validatePageRequest(page, size, fromDate, toDate);

        OffsetDateTime fromDateTime = atStartOfDay(fromDate);
        OffsetDateTime toDateTime = atEndOfDay(toDate);
        String normalizedAction = normalize(action);
        String normalizedStatus = normalize(status);

        Specification<AuditLogEntity> specification = Specification.where(hasAction(normalizedAction))
                .and(hasStatus(normalizedStatus))
                .and(createdAtOnOrAfter(fromDateTime))
                .and(createdAtOnOrBefore(toDateTime));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLogResponse> responsePage = auditLogRepository.findAll(specification, pageable)
                .map(AuditLogResponse::from);
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

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private OffsetDateTime atStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private OffsetDateTime atEndOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().minusNanos(1);
    }

    private Specification<AuditLogEntity> hasAction(String action) {
        return (root, query, criteriaBuilder) ->
                action == null ? null : criteriaBuilder.equal(root.get("action"), action);
    }

    private Specification<AuditLogEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<AuditLogEntity> createdAtOnOrAfter(OffsetDateTime fromDateTime) {
        return (root, query, criteriaBuilder) ->
                fromDateTime == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDateTime);
    }

    private Specification<AuditLogEntity> createdAtOnOrBefore(OffsetDateTime toDateTime) {
        return (root, query, criteriaBuilder) ->
                toDateTime == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDateTime);
    }
}
