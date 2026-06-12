package com.minh.notification.health.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.health.application.NotificationServiceHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-service")
@RequiredArgsConstructor
public class NotificationServiceHealthController {

	private final NotificationServiceHealthService notificationServiceHealthService;

	@GetMapping("/health")
	public ApiResponse<NotificationServiceHealthResponse> getHealth() {
		return ApiResponse.success(notificationServiceHealthService.getHealth());
	}
}
