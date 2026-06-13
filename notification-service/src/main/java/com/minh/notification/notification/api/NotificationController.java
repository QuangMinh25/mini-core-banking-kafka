package com.minh.notification.notification.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.notification.application.NotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification log query APIs.")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@Operation(summary = "List notifications", description = "Returns paginated Kafka-driven notification logs with optional status, reference number, and event filters.")
	public ApiResponse<PageResponse<NotificationResponse>> getNotifications(
			@Parameter(description = "Zero-based page index.", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size.", example = "20")
			@RequestParam(defaultValue = "20") int size,
			@Parameter(description = "Notification status filter.", example = "SENT")
			@RequestParam(defaultValue = "") String status,
			@Parameter(description = "Transaction reference number filter.", example = "TXN-20260613-0001")
			@RequestParam(defaultValue = "") String referenceNo,
			@Parameter(description = "Event id filter.", example = "evt-20260613-0001")
			@RequestParam(defaultValue = "") String eventId) {
		return ApiResponse.success(notificationService.getNotifications(page, size, status, referenceNo, eventId));
	}

	@GetMapping("/events/{eventId}")
	@Operation(summary = "Get notification by event id")
	public ApiResponse<NotificationResponse> getNotificationByEventId(@PathVariable String eventId) {
		return ApiResponse.success(notificationService.getNotificationByEventId(eventId));
	}
}
