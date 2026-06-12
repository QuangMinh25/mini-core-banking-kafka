package com.minh.bankingcore.outbox;

import com.minh.bankingcore.common.ApiResponse;
import com.minh.bankingcore.common.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/outbox-events")
public class OutboxEventController {

    private final OutboxEventQueryService outboxEventQueryService;

    public OutboxEventController(OutboxEventQueryService outboxEventQueryService) {
        this.outboxEventQueryService = outboxEventQueryService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OutboxEventResponse>> listOutboxEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(outboxEventQueryService.listOutboxEvents(page, size, status));
    }

    @GetMapping("/{eventId}")
    public ApiResponse<OutboxEventDetailResponse> getOutboxEvent(@PathVariable String eventId) {
        return ApiResponse.success(outboxEventQueryService.getOutboxEvent(eventId));
    }
}
