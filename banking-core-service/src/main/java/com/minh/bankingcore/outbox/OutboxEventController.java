package com.minh.bankingcore.outbox;

import com.minh.bankingcore.common.ApiResponse;
import com.minh.bankingcore.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/outbox-events")
@Tag(name = "Outbox Events", description = "Outbox event query APIs.")
public class OutboxEventController {

    private final OutboxEventQueryService outboxEventQueryService;

    public OutboxEventController(OutboxEventQueryService outboxEventQueryService) {
        this.outboxEventQueryService = outboxEventQueryService;
    }

    @GetMapping
    @Operation(summary = "List outbox events", description = "Returns paginated outbox events with optional status filtering.")
    public ApiResponse<PageResponse<OutboxEventResponse>> listOutboxEvents(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size.", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Outbox event status filter.", example = "PUBLISHED")
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(outboxEventQueryService.listOutboxEvents(page, size, status));
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get outbox event by event id")
    public ApiResponse<OutboxEventDetailResponse> getOutboxEvent(@PathVariable String eventId) {
        return ApiResponse.success(outboxEventQueryService.getOutboxEvent(eventId));
    }
}
