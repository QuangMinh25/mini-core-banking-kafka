package com.minh.notification.processed_event.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.processed_event.application.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/processed-events")
@RequiredArgsConstructor
public class ProcessedEventController {

	private final ProcessedEventService processedEventService;

	@GetMapping
	public ApiResponse<PageResponse<ProcessedEventResponse>> getProcessedEvents(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "") String topic,
			@RequestParam(defaultValue = "") String eventId) {
		return ApiResponse.success(processedEventService.getProcessedEvents(page, size, topic, eventId));
	}

	@GetMapping("/{eventId}")
	public ApiResponse<ProcessedEventResponse> getProcessedEventByEventId(@PathVariable String eventId) {
		return ApiResponse.success(processedEventService.getProcessedEventByEventId(eventId));
	}
}
