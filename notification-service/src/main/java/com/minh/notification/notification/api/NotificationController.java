package com.minh.notification.notification.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ApiResponse<PageResponse<NotificationResponse>> getNotifications(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "") String status,
			@RequestParam(defaultValue = "") String referenceNo,
			@RequestParam(defaultValue = "") String eventId) {
		return ApiResponse.success(notificationService.getNotifications(page, size, status, referenceNo, eventId));
	}

	@GetMapping("/events/{eventId}")
	public ApiResponse<NotificationResponse> getNotificationByEventId(@PathVariable String eventId) {
		return ApiResponse.success(notificationService.getNotificationByEventId(eventId));
	}
}
