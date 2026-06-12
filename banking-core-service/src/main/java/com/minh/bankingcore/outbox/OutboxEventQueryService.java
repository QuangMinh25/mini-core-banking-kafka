package com.minh.bankingcore.outbox;

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

import java.util.Locale;

@Service
public class OutboxEventQueryService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventQueryService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<OutboxEventResponse> listOutboxEvents(int page, int size, String status) {
        validatePageRequest(page, size);
        OutboxEventStatus outboxEventStatus = parseStatus(status);

        Specification<OutboxEventEntity> specification = (root, query, criteriaBuilder) ->
                outboxEventStatus == null ? null : criteriaBuilder.equal(root.get("status"), outboxEventStatus);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OutboxEventResponse> responsePage = outboxEventRepository.findAll(specification, pageable)
                .map(OutboxEventResponse::from);
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public OutboxEventDetailResponse getOutboxEvent(String eventId) {
        return outboxEventRepository.findByEventId(eventId)
                .map(OutboxEventDetailResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.OUTBOX_EVENT_NOT_FOUND));
    }

    private void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "size must be greater than 0");
        }
    }

    private OutboxEventStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return OutboxEventStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Unsupported outbox status: " + status);
        }
    }
}
