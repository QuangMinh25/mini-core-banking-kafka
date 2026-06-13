package com.minh.notification.processed_event.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.processed_event.application.ProcessedEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/processed-events")
@RequiredArgsConstructor
@Tag(name = "Processed Events", description = "Processed Kafka event tracking APIs.")
public class ProcessedEventController {

	private final ProcessedEventService processedEventService;

	@GetMapping
	@Operation(summary = "List processed events", description = "Returns paginated processed-event records with optional topic and event filters.")
	public ApiResponse<PageResponse<ProcessedEventResponse>> getProcessedEvents(
			@Parameter(description = "Zero-based page index.", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size.", example = "20")
			@RequestParam(defaultValue = "20") int size,
			@Parameter(description = "Kafka topic filter.", example = "transaction.completed")
			@RequestParam(defaultValue = "") String topic,
			@Parameter(description = "Event id filter.", example = "evt-20260613-0001")
			@RequestParam(defaultValue = "") String eventId) {
		return ApiResponse.success(processedEventService.getProcessedEvents(page, size, topic, eventId));
	}

	@GetMapping("/{eventId}")
	@Operation(summary = "Get processed event by event id")
	public ApiResponse<ProcessedEventResponse> getProcessedEventByEventId(@PathVariable String eventId) {
		return ApiResponse.success(processedEventService.getProcessedEventByEventId(eventId));
	}
}
